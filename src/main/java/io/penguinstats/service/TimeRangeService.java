package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.enums.Server;
import io.penguinstats.model.TimeRange;

public interface TimeRangeService {

	@Caching(evict = {@CacheEvict(value = "lists", key = "'timeRangeList'"),
			@CacheEvict(value = "maps", key = "'timeRangeMap'")})
	void saveTimeRange(TimeRange timeRange);

	TimeRange getTimeRangeByRangeID(String rangeID);

	@Cacheable(value = "lists", key = "'timeRangeList'")
	List<TimeRange> getAllTimeRanges();

	@Cacheable(value = "maps", key = "'timeRangeMap'")
	Map<String, TimeRange> getTimeRangeMap();

	@Cacheable(value = "maps", key = "'latestTimeRangeMap_' + #server", condition = "#filter == null")
	Map<String, List<Pair<String, List<TimeRange>>>> getLatestMaxAccumulatableTimeRangesMapByServer(Server server);

	List<TimeRange> getSplittedTimeRanges(Server server, String stageId, Long start, Long end);

}
