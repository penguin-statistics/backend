package io.penguinstats.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Override
	public void saveDropInfo(DropInfo dropInfo) {
		dropInfoDao.save(dropInfo);
	}

	@Override
	public Map<String, List<TimeRange>> getLatestMaxAccumulatableTimeRangesMapByServer(Server server) {
		Map<String, List<TimeRange>> result = new HashMap<>();
		Map<String, Map<String, TimeRange>> helper = new HashMap<>();
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		List<DropInfo> infos = dropInfoDao.findDropInfosByServer(server);
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

	@Override
	public Map<String, List<TimeRange>> getActualTimeRangesByServerAndStageId(Server server, String stageId,
			TimeRange timeRange) {
		Map<String, List<TimeRange>> result = new HashMap<>();
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		List<DropInfo> infos = dropInfoDao.findDropInfosByServerAndStageId(server, stageId);
		infos.forEach(info -> {
			TimeRange range = timeRangeMap.get(info.getTimeRangeID());
			String itemId = info.getItemId();
			if (itemId != null && timeRange.isInclude(range)) {
				List<TimeRange> ranges = result.getOrDefault(itemId, new ArrayList<>());
				ranges.add(range);
				result.put(itemId, ranges);
			}
		});
		return result;
	}

	@Override
	public Map<String, Set<String>> getDropSetMap(Server server, Long time) {
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		List<DropInfo> infos = dropInfoDao.findDropInfosByServer(server);
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

	@Override
	public Set<String> getDropSet(Server server, String stageId, Long time) {
		Map<String, Set<String>> dropsetMap = getDropSetMap(server, time);
		return dropsetMap.get(stageId);
	}

}
