package io.penguinstats.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.TimeRangeDao;
import io.penguinstats.enums.ErrorCode;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;
import io.penguinstats.model.TimeRange;
import io.penguinstats.util.exception.NotFoundException;

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
		return timeRangeDao.findByRangeID(rangeID).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND,
				"TimeRange[" + rangeID + "] is not found", Optional.of(rangeID)));
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

	// TOOD: fix comments
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
	 * @return Map<String, List<Pair<String, List<TimeRange>>>>
	 */
	@Override
	public Map<String, List<Pair<String, List<TimeRange>>>>
			getLatestMaxAccumulatableTimeRangesMapByServer(Server server) {
		Map<String, TimeRange> timeRangeMap = getSpringProxy().getTimeRangeMap();
		List<DropInfo> infos = dropInfoService.getDropInfosByServer(server);
		infos.forEach(info -> {
			TimeRange range = timeRangeMap.get(info.getTimeRangeID());
			info.setTimeRange(range);
		});
		Map<String, List<DropInfo>> infosMapStageId = infos.stream().collect(groupingBy(DropInfo::getStageId));
		Map<String, List<Pair<String, List<TimeRange>>>> result = new HashMap<>();
		infosMapStageId.forEach((stageId, infosInOneStage) -> {
			List<Pair<String, List<TimeRange>>> itemTimeRanges = result.getOrDefault(stageId, new ArrayList<>());
			infosInOneStage = infosInOneStage.stream().filter(info -> info.getItemId() != null).collect(toList());
			Map<String, List<DropInfo>> infosMapByItemId =
					infosInOneStage.stream().collect(groupingBy(DropInfo::getItemId));
			infosMapByItemId.forEach((itemId, infosForOneItem) -> {
				infosForOneItem.sort(
						(info1, info2) -> info1.getTimeRange().getStart().compareTo(info2.getTimeRange().getStart()));
				int pointer = infosForOneItem.size() - 1;
				while (pointer >= 0 && Optional.ofNullable(infosForOneItem.get(pointer).getAccumulatable()).map(b -> b)
						.orElse(true))
					pointer--;

				List<DropInfo> accumulatableInfos = pointer >= infosForOneItem.size() ? new ArrayList<>()
						: new ArrayList<>(infosForOneItem.subList(pointer + 1, infosForOneItem.size()));
				List<TimeRange> rangesForOneItem =
						accumulatableInfos.stream().map(DropInfo::getTimeRange).distinct().collect(toList());
				Pair<String, List<TimeRange>> itemWithRange = Pair.with(itemId, rangesForOneItem);
				itemTimeRanges.add(itemWithRange);
			});
			result.put(stageId, itemTimeRanges);
		});
		return result;
	}

	@Override
	public Map<String, TimeRange> getLatestTimeRangesMapByServer(Server server) {
		Map<String, TimeRange> timeRangeMap = getSpringProxy().getTimeRangeMap();
		List<DropInfo> infos = dropInfoService.getDropInfosByServer(server);
		infos.forEach(info -> {
			TimeRange range = timeRangeMap.get(info.getTimeRangeID());
			info.setTimeRange(range);
		});
		Map<String, List<DropInfo>> infosMapStageId = infos.stream().collect(groupingBy(DropInfo::getStageId));
		Map<String, TimeRange> result = new HashMap<>();
		infosMapStageId.forEach((stageId, infosInOneStage) -> {
			Long maxEnd = Long.MIN_VALUE;
			for (DropInfo info : infosInOneStage) {
				Long end = info.getTimeRange().getEnd();
				if (end == null) {
					maxEnd = null;
					break;
				} else if (end.compareTo(maxEnd) > 0)
					maxEnd = end;
			}
			final Long maxEndFinal = maxEnd;
			infosInOneStage = infosInOneStage.stream()
					.filter(info -> Objects.equals(info.getTimeRange().getEnd(), maxEndFinal)).collect(toList());
			if (!infosInOneStage.isEmpty()) {
				TimeRange range = infosInOneStage.get(0).getTimeRange();
				for (int i = 1, l = infosInOneStage.size(); i < l; i++)
					range = range.intersection(infosInOneStage.get(i).getTimeRange());
				result.put(stageId, range);
			}
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
