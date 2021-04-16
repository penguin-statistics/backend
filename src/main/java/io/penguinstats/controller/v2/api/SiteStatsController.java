package io.penguinstats.controller.v2.api;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant.CacheValue;
import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.controller.v2.response.ItemQuantity;
import io.penguinstats.controller.v2.response.SiteStatsResponse;
import io.penguinstats.controller.v2.response.StageTimes;
import io.penguinstats.enums.Server;
import io.penguinstats.model.Stage;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.StageService;
import io.penguinstats.util.DateUtil;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController("siteStatsController")
@RequestMapping("/api/v2/stats")
@Api(tags = {"Website Statistics"})
public class SiteStatsController {

    @Autowired
    private ItemDropService itemDropService;

    @Autowired
    private StageService stageService;

    // FIXME: should create a redis util class
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation(value = "Get statistical data for the website.")
    @GetMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<SiteStatsResponse>
            getSiteStats(@ApiParam(value = "Indicate which server you want to query. Default is CN.",
                    required = false) @RequestParam(name = "server", required = false,
                            defaultValue = "CN") Server server) {
        final long mills_24h = TimeUnit.DAYS.toMillis(1);

        List<StageTimes> totalStageTimes = null;
        Map<String, Integer> totalStageTimesMap = null;
        if (redisTemplate.hasKey(CacheValue.TOTAL_STAGE_TIMES_MAP + "::" + server)) {
            totalStageTimesMap = itemDropService.getTotalStageTimesMap(server, null);
            totalStageTimes = totalStageTimesMap.entrySet().stream().map(e -> new StageTimes(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        } else {
            return new ResponseEntity<SiteStatsResponse>(new SiteStatsResponse("GENERATING"), HttpStatus.OK);
        }

        List<StageTimes> totalStageTimes_24h = null;
        if (redisTemplate.hasKey(CacheValue.TOTAL_STAGE_TIMES_MAP + "::" + server + "_" + mills_24h) != null) {
            Map<String, Integer> totalStageTimesMap_24h = itemDropService.getTotalStageTimesMap(server, mills_24h);
            totalStageTimes_24h = totalStageTimesMap_24h.entrySet().stream()
                    .map(e -> new StageTimes(e.getKey(), e.getValue())).collect(Collectors.toList());
        } else {
            return new ResponseEntity<SiteStatsResponse>(new SiteStatsResponse("GENERATING"), HttpStatus.OK);
        }

        List<ItemQuantity> totalItemQuantities = null;
        if (redisTemplate.hasKey(CacheValue.TOTAL_ITEM_QUANTITIES_MAP + "::" + server) != null) {
            Map<String, Integer> totalItemQuantitiesMap = itemDropService.getTotalItemQuantitiesMap(server);
            totalItemQuantities = totalItemQuantitiesMap.entrySet().stream()
                    .map(e -> new ItemQuantity(e.getKey(), e.getValue())).collect(Collectors.toList());
        } else
            return new ResponseEntity<SiteStatsResponse>(new SiteStatsResponse("GENERATING"), HttpStatus.OK);

        Integer totalApCost = null;
        if (totalStageTimesMap != null) {
            Map<String, Stage> stageMap = stageService.getStageMap();
            totalApCost = totalStageTimesMap.entrySet().stream().reduce(0,
                    (a, b) -> a + Optional.ofNullable(stageMap.get(b.getKey()))
                            .map(stage -> (Boolean.TRUE.equals(stage.getIsGacha()) ? 0 : stage.getApCost())).orElse(0)
                            * b.getValue(),
                    (a, b) -> a + b);
        }

        List<String> keyNames = Arrays.asList(LastUpdateMapKeyName.TOTAL_STAGE_TIMES_MAP + "_" + server,
                LastUpdateMapKeyName.TOTAL_STAGE_TIMES_MAP + "_" + server + "_" + mills_24h,
                LastUpdateMapKeyName.TOTAL_ITEM_QUANTITIES_MAP + "_" + server);
        Long lastUpdateTime = LastUpdateTimeUtil.findMaxLastUpdateTime(keyNames);

        HttpHeaders headers = new HttpHeaders();
        String lastModified = DateUtil.formatDate(new Date(lastUpdateTime));
        headers.add(HttpHeaders.LAST_MODIFIED, lastModified);

        SiteStatsResponse response =
                new SiteStatsResponse(totalStageTimes, totalStageTimes_24h, totalItemQuantities, totalApCost, null);
        return new ResponseEntity<SiteStatsResponse>(response, headers, HttpStatus.OK);
    }

}
