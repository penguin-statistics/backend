package io.penguinstats.controller.v2.api;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant;
import io.penguinstats.constant.Constant.DefaultValue;
import io.penguinstats.constant.Constant.SystemPropertyKey;
import io.penguinstats.controller.v2.request.RecallLastReportRequest;
import io.penguinstats.controller.v2.request.RecognitionReportRequest;
import io.penguinstats.controller.v2.request.SingleRecognitionDrop;
import io.penguinstats.controller.v2.request.SingleReportRequest;
import io.penguinstats.controller.v2.response.RecognitionReportResponse;
import io.penguinstats.controller.v2.response.SingleReportResponse;
import io.penguinstats.enums.ErrorCode;
import io.penguinstats.enums.Server;
import io.penguinstats.model.Drop;
import io.penguinstats.model.ItemDrop;
import io.penguinstats.model.RecognitionReportError;
import io.penguinstats.model.ScreenshotMetadata;
import io.penguinstats.model.Stage;
import io.penguinstats.model.TypedDrop;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.StageService;
import io.penguinstats.service.SystemPropertyService;
import io.penguinstats.service.UserService;
import io.penguinstats.util.AESUtil;
import io.penguinstats.util.CookieUtil;
import io.penguinstats.util.HashUtil;
import io.penguinstats.util.IpUtil;
import io.penguinstats.util.JSONUtil;
import io.penguinstats.util.RSAUtil;
import io.penguinstats.util.exception.BusinessException;
import io.penguinstats.util.validator.ValidatorContext;
import io.penguinstats.util.validator.ValidatorFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController("reportController_v2")
@RequestMapping("/api/v2/report")
@Api(tags = {"Report"})
public class ReportController {

    @Autowired
    private ItemDropService itemDropService;

    @Autowired
    private UserService userService;

    @Autowired
    private StageService stageService;

    @Autowired
    private SystemPropertyService systemPropertyService;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private ValidatorFacade validatorFacade;

    @Resource(name = "threadPool")
    private ThreadPoolTaskExecutor executor;

    @ApiOperation(value = "Submit a drop report",
            notes = "Detailed instructions can be found at: https://developer.penguin-stats.io/docs/report-api")
    @PostMapping
    public ResponseEntity<SingleReportResponse> saveSingleReport(
            @Valid @RequestBody SingleReportRequest singleReportRequest, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String userID = cookieUtil.readUserIDFromCookie(request);
        if (userID == null) {
            userID = userService.createNewUser(IpUtil.getIpAddr(request));
        }
        try {
            CookieUtil.setUserIDCookie(response, userID);
        } catch (UnsupportedEncodingException e) {
            log.error("Error in handleUserIDFromCookie: uid={}", userID);
        }
        log.info("user " + userID + " POST /report\n"
                + Objects.requireNonNull(JSONUtil.convertObjectToJSONObject(singleReportRequest)).toString(2));

        String stageId = singleReportRequest.getStageId();
        String source = singleReportRequest.getSource();
        String version = singleReportRequest.getVersion();
        Server server = singleReportRequest.getServer();
        Long timestamp = System.currentTimeMillis();
        String ip = IpUtil.getIpAddr(request);
        Integer times = 1;

        // Validation
        ValidatorContext context = new ValidatorContext().setStageId(stageId).setServer(server).setTimes(times)
                .setDrops(singleReportRequest.getDrops()).setTimestamp(timestamp).setIp(ip).setUserID(userID);
        Boolean isReliable = validatorFacade.doValid(context);

        // Combine typed drop list into untyped drop list. Sum up quantities for each item.
        Map<String, Integer> itemIdQuantityMap = singleReportRequest.getDrops().stream()
                .collect(groupingBy(TypedDrop::getItemId, summingInt(TypedDrop::getQuantity)));
        List<Drop> drops =
                itemIdQuantityMap.entrySet().stream().map(e -> new Drop(e.getKey(), e.getValue())).collect(toList());

        // For gacha type stage, the # of times should be the sum of quantities.
        Stage stage = stageService.getStageByStageId(stageId);
        if (stage != null) {
            Boolean isGacha = stage.getIsGacha();
            if (isGacha != null && isGacha) {
                times = 0;
                for (Drop drop : drops) {
                    times += drop.getQuantity();
                }
            }
        }

        ItemDrop itemDrop = new ItemDrop().setStageId(stageId).setServer(server).setTimes(times).setDrops(drops)
                .setTimestamp(timestamp).setIp(ip).setIsReliable(isReliable).setIsDeleted(false).setSource(source)
                .setVersion(version).setUserID(userID);
        itemDropService.saveItemDrop(itemDrop);
        String reportHash = HashUtil.getHash(itemDrop.getId().toString());

        log.debug("Saving itemDrop: \n" + JSONUtil.convertObjectToJSONObject(itemDrop.toNoIDView()).toString(2));

        return new ResponseEntity<SingleReportResponse>(new SingleReportResponse(reportHash), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Submit a batch drop report from screenshot recognition")
    @PostMapping(path = "/recognition")
    public ResponseEntity<RecognitionReportResponse> saveBatchRecognitionReport(@RequestBody String requestBody,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        RecognitionReportRequest recognitionReportRequest = getRecognitionReportRequestFromRequestBody(requestBody);

        String userID = cookieUtil.readUserIDFromCookie(request);
        if (userID == null) {
            userID = userService.createNewUser(IpUtil.getIpAddr(request));
        }
        try {
            CookieUtil.setUserIDCookie(response, userID);
        } catch (UnsupportedEncodingException e) {
            log.error("Error in handleUserIDFromCookie: uid={}", userID);
        }
        log.info("user " + userID + " POST /report/recognition\n"
                + Objects.requireNonNull(JSONUtil.convertObjectToJSONObject(recognitionReportRequest)).toString(2));

        List<RecognitionReportError> errors = new ArrayList<>();
        batchSaveDropsFromRecognitionReportRequest(recognitionReportRequest, request, userID, errors);
        Collections.sort(errors, (err1, err2) -> (err1.getIndex().compareTo(err2.getIndex())));

        RecognitionReportResponse recognitionReportResponse = new RecognitionReportResponse(errors);
        return new ResponseEntity<>(recognitionReportResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Recall the last Report",
            notes = "Recall the last Drop Report by providing its hash value. "
                    + "Notice that you can only recall the *last* report, "
                    + "which in addition will also expire after 24 hours.")
    @PostMapping(path = "/recall")
    public ResponseEntity<String> recallPersonalReport(
            @Valid @RequestBody RecallLastReportRequest recallLastReportRequest, HttpServletRequest request)
            throws Exception {
        String userID = cookieUtil.readUserIDFromCookie(request);
        if (userID == null) {
            log.error("Error in recallPersonalReport: Cannot read user ID");
            throw new BusinessException(ErrorCode.BUSINESS_EXCEPTION, "Cannot read user ID");
        }

        log.info("user " + userID + " POST /report/recall\n");
        itemDropService.recallItemDrop(userID, recallLastReportRequest.getReportHash());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private RecognitionReportRequest getRecognitionReportRequestFromRequestBody(String requestBody) {
        String dataJSONStr = null;
        boolean doneDecryption = false;
        if (JSONUtil.isValidJSON(requestBody)) {
            JSONObject requestObj = new JSONObject(requestBody);
            dataJSONStr = requestObj.toString();
        } else {
            String[] strs = requestBody.split(":");
            if (strs.length != 2) {
                throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Failed to parse request body.");
            }
            String encryptedAESKey = strs[0];
            String encryptedBody = strs[1];
            if (StringUtils.isAnyEmpty(encryptedAESKey, encryptedBody)) {
                throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Invalid request.");
            }
            dataJSONStr = decryptRecgonitionRequest(encryptedAESKey, encryptedBody);
            if (!JSONUtil.isValidJSON(dataJSONStr)) {
                throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Invalid request body.");
            }
            doneDecryption = true;
        }

        RecognitionReportRequest recognitionReportRequest =
                JSONUtil.convertJSONStrToObject(dataJSONStr, RecognitionReportRequest.class);
        if (recognitionReportRequest == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Failed to convert report object.");
        }
        recognitionReportRequest.setDoneDecryption(doneDecryption);
        return recognitionReportRequest;
    }

    private String decryptRecgonitionRequest(String encryptedAESKey, String encryptedBody) {
        String privateKey =
                systemPropertyService.getPropertyStringValue(Constant.SystemPropertyKey.RECOGNITION_PRIVATE_KEY);
        try {
            String decryptedAESKeyBase64 = RSAUtil.decryptDataOnJava(encryptedAESKey, privateKey);
            String decyptedBody = AESUtil.decrypt(encryptedBody, decryptedAESKeyBase64);
            return decyptedBody;
        } catch (Exception e) {
            log.error("Error in decryptRecgonitionRequest.", e);
            return null;
        }
    }

    private void batchSaveDropsFromRecognitionReportRequest(RecognitionReportRequest recognitionReportRequest,
            HttpServletRequest request, String userID, List<RecognitionReportError> errors) {
        String source = recognitionReportRequest.getSource();
        String version = recognitionReportRequest.getVersion();
        Server server = recognitionReportRequest.getServer();
        Long timestamp = System.currentTimeMillis();
        String ip = IpUtil.getIpAddr(request);

        List<SingleRecognitionDrop> batchDrops = recognitionReportRequest.getBatchDrops();
        if (batchDrops == null || batchDrops.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "'batchDrops' cannot be null or empty.");
        }
        Integer maxBatchSize = systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.RECOGNITION_BATCH_MAX,
                DefaultValue.RECOGNITION_BATCH_MAX);
        if (maxBatchSize != null && Integer.compare(batchDrops.size(), maxBatchSize) > 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER,
                    "The size of 'batchDrops' cannot exceed " + maxBatchSize);
        }

        List<ItemDrop> itemDrops = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(batchDrops.size());
        for (int i = 0, l = batchDrops.size(); i < l; i++) {
            SingleRecognitionDrop singleDrop = batchDrops.get(i);
            final int index = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String stageId = singleDrop.getStageId();
                        ScreenshotMetadata screenshotMetadata = singleDrop.getMetadata();
                        String md5 = screenshotMetadata != null ? screenshotMetadata.getMd5() : null;
                        Integer times = 1;

                        // Validation
                        ValidatorContext context = new ValidatorContext().setStageId(stageId).setServer(server)
                                .setTimes(times).setDrops(singleDrop.getDrops()).setTimestamp(timestamp).setIp(ip)
                                .setUserID(userID).setMd5(md5);
                        Boolean isReliable = validatorFacade.doValid(context);

                        // Combine typed drop list into untyped drop list. Sum up quantities for each item.
                        Map<String, Integer> itemIdQuantityMap = singleDrop.getDrops().stream()
                                .collect(groupingBy(TypedDrop::getItemId, summingInt(TypedDrop::getQuantity)));
                        List<Drop> drops = itemIdQuantityMap.entrySet().stream()
                                .map(e -> new Drop(e.getKey(), e.getValue())).collect(toList());

                        // Screenshot recognition does not accept gacha stage for now
                        Stage stage = stageService.getStageByStageId(stageId);
                        if (stage != null) {
                            Boolean isGacha = stage.getIsGacha();
                            if (isGacha != null && isGacha) {
                                times = 0;
                                for (Drop drop : drops) {
                                    times += drop.getQuantity();
                                }
                                isReliable = false;
                            }
                        }

                        // If the request is not from decryption, this record won't be calculated into global data
                        if (!Boolean.TRUE.equals(recognitionReportRequest.getDoneDecryption())) {
                            isReliable = false;
                        }

                        ItemDrop itemDrop = new ItemDrop().setStageId(stageId).setServer(server).setTimes(times)
                                .setDrops(drops).setTimestamp(timestamp).setIp(ip).setIsReliable(isReliable)
                                .setIsDeleted(false).setSource(source).setVersion(version).setUserID(userID)
                                .setScreenshotMetadata(screenshotMetadata);
                        itemDrops.add(itemDrop);
                    } catch (Exception e) {
                        log.error("Error in batchSaveDropsFromRecognitionReportRequest");
                        errors.add(new RecognitionReportError(index, e.getMessage()));
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (Exception e) {
            log.error("Error in batchSaveDropsFromRecognitionReportRequest", e);
        }
        itemDropService.batchSaveItemDrops(itemDrops);
    }

}
