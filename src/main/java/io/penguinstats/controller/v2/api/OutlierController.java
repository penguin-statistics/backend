package io.penguinstats.controller.v2.api;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant;
import io.penguinstats.constant.Constant.DefaultValue;
import io.penguinstats.constant.Constant.OutlierUpYunSignature;
import io.penguinstats.constant.Constant.SiteURL;
import io.penguinstats.constant.Constant.SystemPropertyKey;
import io.penguinstats.controller.v2.response.PostOutlierResponse;
import io.penguinstats.enums.ErrorCode;
import io.penguinstats.model.Outlier;
import io.penguinstats.service.OutlierService;
import io.penguinstats.service.SystemPropertyService;
import io.penguinstats.service.UserService;
import io.penguinstats.util.CookieUtil;
import io.penguinstats.util.IpUtil;
import io.penguinstats.util.JSONUtil;
import io.penguinstats.util.UpYunUtil;
import io.penguinstats.util.exception.BusinessException;
import io.penguinstats.util.exception.ServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController("outlierController_v2")
@RequestMapping("/api/v2/outliers")
@Api(tags = {"Outlier"})
public class OutlierController {

    @Autowired
    private OutlierService outlierService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemPropertyService systemPropertyService;

    @Autowired
    private CookieUtil cookieUtil;

    @ApiOperation(value = "Submit an outlier")
    @PostMapping
    public ResponseEntity<PostOutlierResponse> postOutlier(@Valid @RequestBody String postOutlierRequest,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!JSONUtil.isValidJSON(postOutlierRequest)) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Invalid metadata.");
        }

        String userID = cookieUtil.readUserIDFromCookie(request);
        if (userID == null) {
            userID = userService.createNewUser(IpUtil.getIpAddr(request));
        }
        try {
            CookieUtil.setUserIDCookie(request, response, userID);
        } catch (UnsupportedEncodingException e) {
            log.error("Error in handleUserIDFromCookie: uid={}", userID);
        }
        log.info("user " + userID + " POST /outliers");

        String bucket = systemPropertyService.getPropertyStringValue(SystemPropertyKey.OUTLIER_IMG_BUCKET);
        String saveKey = systemPropertyService.getPropertyStringValue(SystemPropertyKey.OUTLIER_IMG_SAVE_KEY);
        String userName = systemPropertyService.getPropertyStringValue(SystemPropertyKey.OUTLIER_IMG_USERNAME);
        String password = systemPropertyService.getPropertyStringValue(SystemPropertyKey.OUTLIER_IMG_PASSWORD);
        int expireMin = systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.OUTLIER_IMG_EXPIRATION,
                DefaultValue.OUTLIER_IMG_EXPIRATION);

        Document doc = Document.parse(postOutlierRequest);
        Outlier outlier = outlierService.saveOutlier(doc, bucket, userID);
        String _id = outlier.getId().toString();
        long expiration = Instant.now().plusSeconds(TimeUnit.MINUTES.toSeconds(expireMin)).toEpochMilli();

        String notifyUrl = new StringBuilder().append(SiteURL.PENGUIN_STATS_IO).append("PenguinStats")
                .append(Constant.API_V2).append("/outliers/callback?id=").append(_id).toString();
        String policy = UpYunUtil.getPolicy(bucket, saveKey, expiration, notifyUrl);
        String authorization = UpYunUtil.getAuthorization(OutlierUpYunSignature.METHOD_POST, null, "/" + bucket, policy,
                userName, password, null);

        return new ResponseEntity<PostOutlierResponse>(new PostOutlierResponse(bucket, policy, authorization),
                HttpStatus.OK);
    }

    @ApiOperation(value = "Handle callback from UpYun. Save path and time for the screenshot uploaded.")
    @PostMapping(path = "/callback")
    public ResponseEntity<String> handleUpYunCallback(@RequestParam(name = "id") String _id,
            @RequestParam(name = "code") Integer code, @RequestParam(name = "message") String message,
            @RequestParam(name = "url") String url, @RequestParam(name = "time") Long time,
            @RequestHeader("content-md5") String md5, @RequestHeader("authorization") String authorization,
            HttpServletRequest request) throws Exception {
        if (StringUtils.isAnyEmpty(_id, message, url, md5, authorization) || time == null || code == null) {
            log.error("Missing parameters or headers in UpYun callback.");
            throw new BusinessException(ErrorCode.INVALID_PARAMETER,
                    "Missing parameters or headers in UpYun callback.");
        }

        if (200 != code || !"ok".equals(message)) {
            log.error("Failed to upload screenshot to UpYun. code = {}, message = {}", code, message);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        String userName = systemPropertyService.getPropertyStringValue(SystemPropertyKey.OUTLIER_IMG_USERNAME);
        String password = systemPropertyService.getPropertyStringValue(SystemPropertyKey.OUTLIER_IMG_PASSWORD);

        Long dateTime = TimeUnit.SECONDS.toMillis(time);
        String path = request.getRequestURI() + "?" + request.getQueryString();

        String calculatedAuth = UpYunUtil.getAuthorization(OutlierUpYunSignature.METHOD_POST, dateTime, path, null,
                userName, password, md5);
        if (!authorization.equals(calculatedAuth)) {
            log.error("Signature error. time = {}, path = {}, md5 = {}, authorization = {}, calculatedAuth = {}", time,
                    path, md5, authorization, calculatedAuth);
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Signature error.");
        }

        Outlier outlier = outlierService.getOutlierById(_id);
        if (!StringUtils.isEmpty(outlier.getUrl()) || outlier.getTime() != null) {
            log.error("Outlier {} has been already updated with url and time.", _id);
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "Outlier " + _id + " has been already updated with url and time.");
        }
        outlier.setUrl(url);
        outlier.setTime(dateTime);
        outlierService.saveOutlier(outlier);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
