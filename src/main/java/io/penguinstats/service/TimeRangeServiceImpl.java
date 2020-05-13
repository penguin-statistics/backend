package io.penguinstats.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.TimeRangeDao;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;
import io.penguinstats.model.TimeRange;

@Service("timeRangeService")
public class TimeRangeServiceImpl implements TimeRangeService {

	@Autowired
	private TimeRangeDao timeRangeDao;
	@Autowired
	private DropInfoService dropInfoService;
	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public void saveTimeRange(TimeRange timeRange) {
		timeRangeDao.save(timeRange);
	}

	@Override
	public TimeRange getTimeRangeByRangeID(String rangeID) {
		return timeRangeDao.findByRangeID(rangeID);
	}

	/**
	 * @Title: getAllTimeRanges
	 * @Description: Return all time ranges in the database as a list.
	 * @return List<TimeRange>
	 */
	@Override
	public List<TimeRange> getAllTimeRanges() {
		List<TimeRange> ranges = timeRangeDao.findAll();
		return ranges;
	}

	/**
	 * @Title: getTimeRangeMap
	 * @Description: Return a map which has rangeID as key and time range object as value.
	 * @return Map<String,TimeRange>
	 */
	@Override
	public Map<String, TimeRange> getTimeRangeMap() {
		List<TimeRange> list = getAllTimeRanges();
		Map<String, TimeRange> map = new HashMap<>();
		list.forEach(range -> map.put(range.getRangeID(), range));
		return map;
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
		Map<String, TimeRange> timeRangeMap = getSpringProxy().getTimeRangeMap();
		List<DropInfo> infos = dropInfoService.getDropInfosByServer(server);
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
	public List<TimeRange> getSplittedTimeRanges(Server server, String stageId, Long start, Long end) {
		Map<String, TimeRange> timeRangeMap = getSpringProxy().getTimeRangeMap();
		List<DropInfo> infos = dropInfoService.getDropInfosByServerAndStageId(server, stageId);
		TimeRange givenTimeRange = new TimeRange(start, end);
		return infos.stream().map(info -> timeRangeMap.get(info.getTimeRangeID())).filter(range -> range != null)
				.distinct().map(range -> range.intersection(givenTimeRange)).filter(range -> range != null)
				.collect(Collectors.toList());
	}

	/** 
	 * @Title: getSpringProxy 
	 * @Description: Use proxy to hit cache 
	 * @return TimeRangeService
	 */
	private TimeRangeService getSpringProxy() {
		return applicationContext.getBean(TimeRangeService.class);
	}

}
