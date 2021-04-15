package io.penguinstats.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.constant.Constant.CacheValue;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;

public interface DropInfoService {

    @Caching(evict = {@CacheEvict(value = CacheValue.DROP_INFO_LIST, key = "#dropInfo.server"),
            @CacheEvict(value = CacheValue.LATEST_MAX_ACCUMULATABLE_TIME_RANGE_MAP, key = "#dropInfo.server"),
            @CacheEvict(value = CacheValue.LATEST_TIME_RANGE_MAP, key = "#dropInfo.server"),
            @CacheEvict(value = CacheValue.DROP_SET, allEntries = true)})
    void saveDropInfo(DropInfo dropInfo);

    void batchSave(List<DropInfo> infos);

    @Cacheable(value = CacheValue.DROP_INFO_LIST, key = "#server", condition = "#filter == null")
    List<DropInfo> getDropInfosByServer(Server server);

    @Cacheable(value = CacheValue.DROP_INFO_LIST, key = "#server + '_' + #stageId", condition = "#filter == null")
    List<DropInfo> getDropInfosByServerAndStageId(Server server, String stageId);

    @Cacheable(value = CacheValue.DROP_INFO_LIST, key = "#server + '_' + #timeRangeID", condition = "#filter == null")
    List<DropInfo> getDropInfosByServerAndTimeRangeID(Server server, String timeRangeID);

    @Cacheable(value = CacheValue.LATEST_DROP_INFO_MAP, key = "#server", condition = "#filter == null")
    public Map<String, List<DropInfo>> getLatestDropInfosMapByServer(Server server);

    Map<String, Set<String>> getDropSetMap(Server server, Long time);

    @Cacheable(value = CacheValue.DROP_SET, key = "#server + '_' + #stageId + '_' + #time",
            condition = "#filter == null")
    Set<String> getDropSet(Server server, String stageId, Long time);

    Map<String, List<DropInfo>> getOpeningDropInfosMap(Server server, Long time, boolean excludeRecognitionOnly);

    Set<String> getOpeningStages(Server server, Long time);

}
