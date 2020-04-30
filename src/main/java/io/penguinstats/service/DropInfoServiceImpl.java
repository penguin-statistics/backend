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
	public Map<String, TimeRange> getLatestTimeRangeMapByServer(Server server) {
		Map<String, TimeRange> result = new HashMap<>();
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		List<DropInfo> infos = dropInfoDao.findDropInfosByServer(server);
		infos.forEach(info -> {
			TimeRange range = timeRangeMap.get(info.getTimeRangeID());
			String stageId = info.getStageId();
			if (!result.containsKey(stageId) || result.get(stageId).getStart().compareTo(range.getStart()) < 0)
				result.put(stageId, range);
		});
		return result;
	}

	@Override
	public Map<String, TimeRange> getSecondLastTimeRangeMapByServer(Server server) {
		Map<String, TimeRange> secondLastTimeRangeMap = new HashMap<>();
		Map<String, TimeRange> latestTimeRangeMap = new HashMap<>();
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		List<DropInfo> infos = dropInfoDao.findDropInfosByServer(server);
		infos.forEach(info -> {
			TimeRange range = timeRangeMap.get(info.getTimeRangeID());
			String stageId = info.getStageId();
			if (!latestTimeRangeMap.containsKey(stageId)) {
				latestTimeRangeMap.put(stageId, range);
				secondLastTimeRangeMap.put(stageId, range);
			} else if (latestTimeRangeMap.get(stageId).getStart().compareTo(range.getStart()) < 0) {
				secondLastTimeRangeMap.put(stageId, latestTimeRangeMap.get(stageId));
				latestTimeRangeMap.put(stageId, range);
			}
		});
		return secondLastTimeRangeMap;
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
	public Map<String, Set<String>> getDropSet(Server server, Long time) {
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

}
