package io.penguinstats.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;

import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;
import io.penguinstats.model.TimeRange;

public interface DropInfoService {

	void saveDropInfo(DropInfo dropInfo);

	@Cacheable(value = "latest-time-range-map", key = "#server", condition = "#filter == null")
	Map<String, List<TimeRange>> getLatestMaxAccumulatableTimeRangesMapByServer(Server server);

	//	@Cacheable(value = "second-last-time-range-map", key = "#server", condition = "#filter == null")
	//	Map<String, TimeRange> getSecondLastTimeRangeMapByServer(Server server);

	Map<String, List<TimeRange>> getActualTimeRangesByServerAndStageId(Server server, String stageId,
			TimeRange timeRange);

	@Cacheable(value = "dropset-map", key = "#server + '_' + time", condition = "#filter == null")
	Map<String, Set<String>> getDropSetMap(Server server, Long time);

	Set<String> getDropSet(Server server, String stageId, Long time);

}
