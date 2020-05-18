package io.penguinstats.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.stream.Collectors;

import lombok.Setter;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.TimeRangeDao;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;
import io.penguinstats.model.TimeRange;
/**
 * @author AlvISsReimu
 */
@Setter(onMethod =@__(@Autowired))
@Service("timeRangeService")
public class TimeRangeServiceImpl implements TimeRangeService {

	private TimeRangeDao timeRangeDao;
	private DropInfoService dropInfoService;
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
		return timeRangeDao.findAll();
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

	// TODO: fix comments
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
	 * @param server the server uploader belongs to
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
						Comparator.comparing(info -> info.getTimeRange().getStart()));
				int pointer = infosForOneItem.size() - 1;
				while (pointer >= 0 && Optional.ofNullable(infosForOneItem.get(pointer).getAccumulatable()).orElse(true)) {
					pointer--;
				}

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
	public List<TimeRange> getSplittedTimeRanges(Server server, String stageId, Long start, Long end) {
		Map<String, TimeRange> timeRangeMap = getSpringProxy().getTimeRangeMap();
		List<DropInfo> infos = dropInfoService.getDropInfosByServerAndStageId(server, stageId);
		TimeRange givenTimeRange = new TimeRange(start, end);
		return infos.stream().map(info -> timeRangeMap.get(info.getTimeRangeID())).filter(Objects::nonNull)
				.distinct().map(range -> range.intersection(givenTimeRange)).filter(Objects::nonNull)
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
