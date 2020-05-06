package io.penguinstats.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.DropInfoDao;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;
import io.penguinstats.model.TimeRange;

@Service("dropInfoService")
public class DropInfoServiceImpl implements DropInfoService {

	@Autowired
	private DropInfoDao dropInfoDao;
	@Autowired
	private TimeRangeService timeRangeService;
	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public void saveDropInfo(DropInfo dropInfo) {
		dropInfoDao.save(dropInfo);
	}

	@Override
	public List<DropInfo> getDropInfosByServer(Server server) {
		return dropInfoDao.findDropInfosByServer(server);
	}

	/** 
	 * @Title: getLatestMaxAccumulatableTimeRangesMapByServer 
	 * @Description: Latest max accumulatable time ranges map, key is stageId.
	 *               For example we have 5 time ranges for a stage:
	 *               Range I, Time 0~8, droplist: A, B
	 *               Range II, Time 10~15, droplist: A, B, C
	 *               Range III, Time 15~20, droplist: A, B
	 *               Range IV, Time 20~25, droplist: A, B, D
	 *               Range V, Time 30~present, droplist: A, B
	 *               C is a new material, we think it may affect others' drop rates.
	 *               D is AP supplement item, we think it is independent from others.
	 *               Thus, data from Range I should not be calculated into the global matrix.
	 *               D does not affect drop rates, so data from Range IV can be combined with II, III and V.
	 *               So the longest, and accumulatable time ranges are: II, III, IV and V.
	 * @param server
	 * @return Map<String,List<TimeRange>>
	 */
	@Override
	public Map<String, List<TimeRange>> getLatestMaxAccumulatableTimeRangesMapByServer(Server server) {
		Map<String, List<TimeRange>> result = new HashMap<>();
		Map<String, Map<String, TimeRange>> helper = new HashMap<>();
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		List<DropInfo> infos = getSpringProxy().getDropInfosByServer(server);
		infos.forEach(info -> {
			TimeRange range = timeRangeMap.get(info.getTimeRangeID());
			String stageId = info.getStageId();
			Map<String, TimeRange> subMap = helper.getOrDefault(stageId, new HashMap<>());
			subMap.putIfAbsent(range.getRangeID(), range);
			helper.put(stageId, subMap);
		});
		helper.forEach((stageId, subMap) -> {
			subMap.forEach((rangeID, range) -> {
				List<TimeRange> ranges = result.getOrDefault(stageId, new ArrayList<>());
				ranges.add(range);
				result.put(stageId, ranges);
			});
		});
		result.forEach((stageId, ranges) -> {
			ranges.sort((r1, r2) -> r1.getStart().compareTo(r2.getStart()));
			int pointer = ranges.size() - 1;
			while (pointer > 0 && ranges.get(pointer).getAccumulatable())
				pointer--;
			result.put(stageId, new ArrayList<>(ranges.subList(pointer, ranges.size())));
		});
		return result;
	}

	/** 
	 * @Title: getDropSetMap 
	 * @Description: Get all dropsets in a map, key is stageId
	 * @param server
	 * @param time
	 * @return Map<String,Set<String>>
	 */
	@Override
	public Map<String, Set<String>> getDropSetMap(Server server, Long time) {
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		List<DropInfo> infos = getSpringProxy().getDropInfosByServer(server);
		Map<String, Set<String>> result = new HashMap<>();
		infos.forEach(info -> {
			String itemId = info.getItemId();
			String stageId = info.getStageId();
			TimeRange range = timeRangeMap.get(info.getTimeRangeID());
			if (itemId != null && range.isIn(time)) {
				Set<String> dropSet = result.getOrDefault(stageId, new HashSet<>());
				dropSet.add(itemId);
				result.put(stageId, dropSet);
			}
		});
		return result;
	}

	/** 
	 * @Title: getDropSet 
	 * @Description: Get all dropped itemIds in a set, under given server, stage and time
	 * @param server
	 * @param stageId
	 * @param time
	 * @return Set<String>
	 */
	@Override
	public Set<String> getDropSet(Server server, String stageId, Long time) {
		Map<String, Set<String>> dropsetMap = getDropSetMap(server, time);
		return dropsetMap.get(stageId);
	}

	/** 
	 * @Title: getOpeningDropInfosMap 
	 * @Description: Get lists of dropInfos whose stage are opening under the given time and server. key is stageId
	 * @param server
	 * @param time
	 * @return Map<String,List<DropInfo>>
	 */
	@Override
	public Map<String, List<DropInfo>> getOpeningDropInfosMap(Server server, Long time) {
		Map<String, List<DropInfo>> result = new HashMap<>();
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		List<DropInfo> infos = getSpringProxy().getDropInfosByServer(server);
		infos.forEach(info -> {
			TimeRange range = timeRangeMap.get(info.getTimeRangeID());
			if (range != null && range.isIn(time)) {
				String stageId = info.getStageId();
				List<DropInfo> list = result.getOrDefault(stageId, new ArrayList<>());
				list.add(info);
				result.put(stageId, list);
			}
		});
		return result;
	}

	/** 
	 * @Title: getOpeningStages 
	 * @Description: Get a list of stageIds which are opening under the given time and server
	 * @param server
	 * @param time
	 * @return Set<String>
	 */
	@Override
	public Set<String> getOpeningStages(Server server, Long time) {
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		List<DropInfo> infos = getSpringProxy().getDropInfosByServer(server);
		Set<String> stageIds = new HashSet<>();
		infos.forEach(info -> {
			String stageId = info.getStageId();
			if (!stageIds.contains(stageId)) {
				TimeRange range = timeRangeMap.get(info.getTimeRangeID());
				if (range != null && range.isIn(time)) {
					stageIds.add(stageId);
				}
			}
		});
		return stageIds;
	}

	/** 
	 * @Title: getSpringProxy 
	 * @Description: Use proxy to hit cache 
	 * @return DropInfoService
	 */
	private DropInfoService getSpringProxy() {
		return applicationContext.getBean(DropInfoService.class);
	}

}
