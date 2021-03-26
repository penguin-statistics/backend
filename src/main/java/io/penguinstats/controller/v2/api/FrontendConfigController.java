package io.penguinstats.controller.v2.api;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.service.FrontendConfigService;
import io.penguinstats.util.DateUtil;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("configController_v2")
@RequestMapping("/api/v2/configs")
@Api(tags = {"Config"})
public class FrontendConfigController {

    @Autowired
    private FrontendConfigService frontendConfigService;

    @ApiOperation(value = "Get frontend configs")
    @GetMapping(produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, String>> getFrontendConfigs() {
        Map<String, String> map = frontendConfigService.getFrontendConfigMap();
        HttpHeaders headers = new HttpHeaders();
        String lastModified = DateUtil
                .formatDate(new Date(LastUpdateTimeUtil.getLastUpdateTime(LastUpdateMapKeyName.FRONTEND_CONFIG_MAP)));
        headers.add(HttpHeaders.LAST_MODIFIED, lastModified);
        return new ResponseEntity<>(map, headers, HttpStatus.OK);
    }

}
