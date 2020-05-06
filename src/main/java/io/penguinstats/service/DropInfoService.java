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

	@Cacheable(value = "lists", key = "'dropInfoList_' + #server", condition = "#filter == null")
	List<DropInfo> getDropInfosByServer(Server server);

	@Cacheable(value = "maps", key = "'latestTimeRangeMap_' + #server", condition = "#filter == null")
	Map<String, List<TimeRange>> getLatestMaxAccumulatableTimeRangesMapByServer(Server server);

	Map<String, Set<String>> getDropSetMap(Server server, Long time);

	@Cacheable(value = "sets", key = "'dropSet_' + #server + '_' + #stageId + '_' + #time",
			condition = "#filter == null")
	Set<String> getDropSet(Server server, String stageId, Long time);

	Map<String, List<DropInfo>> getOpeningDropInfosMap(Server server, Long time);

	Set<String> getOpeningStages(Server server, Long time);

}
