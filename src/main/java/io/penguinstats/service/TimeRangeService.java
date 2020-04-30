package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.model.TimeRange;

public interface TimeRangeService {

	@Caching(evict = {@CacheEvict(value = "lists", key = "'timeRangeList'"),
			@CacheEvict(value = "maps", key = "'timeRangeMap'"),
			@CacheEvict(value = "time-range", key = "#timeRange.rangeID")})
	void saveTimeRange(TimeRange timeRange);

	@Cacheable(value = "time-range", key = "#rangeID", unless = "#result == null")
	TimeRange getTimeRangeByRangeID(String rangeID);

	@Cacheable(value = "lists", key = "'timeRangeList'")
	List<TimeRange> getAllTimeRanges();

	@Cacheable(value = "maps", key = "'timeRangeMap'")
	Map<String, TimeRange> getTimeRangeMap();

}
