package io.penguinstats.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;

public interface DropInfoService {

	/**
	 * @Description: save drop record
	 * @param dropInfo drop info include itemid,server,stageid
	 */
	@Caching(evict = {@CacheEvict(value = "lists", key = "'dropInfoList_' + #dropInfo.server"),
			@CacheEvict(value = "maps", key = "'latestTimeRangeMap_' + #dropInfo.server"),
			@CacheEvict(value = "sets", allEntries = true)})
	void saveDropInfo(DropInfo dropInfo);

	@Cacheable(value = "lists", key = "'dropInfoList_' + #server", condition = "#filter == null")
	List<DropInfo> getDropInfosByServer(Server server);

	@Cacheable(value = "lists", key = "'dropInfoList_' + #server + '_' + #stageId", condition = "#filter == null")
	List<DropInfo> getDropInfosByServerAndStageId(Server server, String stageId);

	@Cacheable(value = "maps", key = "'latestDropInfosMap_' + #server", condition = "#filter == null")
	public Map<String, List<DropInfo>> getLatestDropInfosMapByServer(Server server);

	Map<String, Set<String>> getDropSetMap(Server server, Long time);

	@Cacheable(value = "sets", key = "'dropSet_' + #server + '_' + #stageId + '_' + #time",
			condition = "#filter == null")
	Set<String> getDropSet(Server server, String stageId, Long time);

	Map<String, List<DropInfo>> getOpeningDropInfosMap(Server server, Long time);

	Set<String> getOpeningStages(Server server, Long time);

}
