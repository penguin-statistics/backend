package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.constant.Constant.CacheValue;
import io.penguinstats.enums.Server;
import io.penguinstats.model.TimeRange;

public interface TimeRangeService {

    @Caching(evict = {@CacheEvict(value = CacheValue.LISTS, key = "'timeRangeList'"),
            @CacheEvict(value = CacheValue.MAPS, key = "'timeRangeMap'")})
    void saveTimeRange(TimeRange timeRange);

    TimeRange getTimeRangeByRangeID(String rangeID);

    @Cacheable(value = CacheValue.LISTS, key = "'timeRangeList'")
    List<TimeRange> getAllTimeRanges();

    @Cacheable(value = CacheValue.MAPS, key = "'timeRangeMap'")
    Map<String, TimeRange> getTimeRangeMap();

    @Cacheable(value = CacheValue.LATEST_MAX_ACCUMULATABLE_TIME_RANGE_MAP, key = "#server")
    Map<String, Map<String, List<String>>> getLatestMaxAccumulatableTimeRangesMapByServer(Server server);

    @Cacheable(value = CacheValue.LATEST_TIME_RANGE_MAP, key = "#server")
    Map<String, TimeRange> getLatestTimeRangesMapByServer(Server server);

    List<TimeRange> getSplittedTimeRanges(Server server, String stageId, Long start, Long end);

    List<TimeRange> getPassedTimeRanges(long time);

}
