package io.penguinstats.controller.v2.api;

import io.penguinstats.controller.v2.request.RecallLastReportRequest;
import io.penguinstats.controller.v2.request.SingleReportRequest;
import io.penguinstats.controller.v2.response.SingleReportResponse;
import io.penguinstats.enums.Server;
import io.penguinstats.model.Drop;
import io.penguinstats.model.ItemDrop;
import io.penguinstats.model.Stage;
import io.penguinstats.model.TypedDrop;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.StageService;
import io.penguinstats.service.UserService;
import io.penguinstats.util.CookieUtil;
import io.penguinstats.util.HashUtil;
import io.penguinstats.util.IpUtil;
import io.penguinstats.util.JSONUtil;
import io.penguinstats.util.validator.ValidatorContext;
import io.penguinstats.util.validator.ValidatorFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;

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
    private CookieUtil cookieUtil;

    @Autowired
    private ValidatorFacade validatorFacade;

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
            log.error("Error in handleUserIDFromCookie: ", e);
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
        List<Drop> drops = itemIdQuantityMap.entrySet().stream().map(e -> new Drop(e.getKey(), e.getValue()))
                .collect(toList());

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

    @ApiOperation(value = "Recall the last Report",
            notes = "Recall the last Drop Report by providing its hash value. "
                    + "Notice that you can only recall the *last* report, "
                    + "which in addition will also expire after 24 hours.")
    @PostMapping(path = "/recall")
    public ResponseEntity<String> recallPersonalReport(
            @Valid @RequestBody RecallLastReportRequest recallLastReportRequest, HttpServletRequest request) throws Exception {
        String userID = cookieUtil.readUserIDFromCookie(request);
        if (userID == null) {
            log.error("Error in recallPersonalReport: Cannot read user ID");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        log.info("user " + userID + " POST /report/recall\n");
        itemDropService.recallItemDrop(userID, recallLastReportRequest.getReportHash());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
