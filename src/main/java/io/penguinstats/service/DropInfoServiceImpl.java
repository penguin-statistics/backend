package io.penguinstats.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.dao.DropInfoDao;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;
import io.penguinstats.model.TimeRange;
import io.penguinstats.util.LastUpdateTimeUtil;
/**
 * @author AlvISsReimu
 */
@Setter(onMethod =@__(@Autowired))
@Service("dropInfoService")
public class DropInfoServiceImpl implements DropInfoService {

	private DropInfoDao dropInfoDao;
	private TimeRangeService timeRangeService;
	private ApplicationContext applicationContext;

	@Override
	public void saveDropInfo(DropInfo dropInfo) {
		dropInfoDao.save(dropInfo);
	}

	@Override
	public List<DropInfo> getDropInfosByServer(Server server) {
		List<DropInfo> result = dropInfoDao.findDropInfosByServer(server);
		LastUpdateTimeUtil.setCurrentTimestamp(LastUpdateMapKeyName.DROP_INFO_LIST + "_" + server);
		return result;
	}

	@Override
	public List<DropInfo> getDropInfosByServerAndStageId(Server server, String stageId) {
		return dropInfoDao.findDropInfosByServerAndStageId(server, stageId);
	}

	/** 
	 * @Title: getLatestDropInfosMapByServer 
	 * @Description: Get lists of the latest drop info in every stage. Key is stageId.
	 * @param server the server uploader belongs to
	 * @return Map<String, List<DropInfo>>
	 */
	@Override
	public Map<String, List<DropInfo>> getLatestDropInfosMapByServer(Server server) {
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		List<DropInfo> infos = dropInfoDao.findDropInfosByServer(server);
		Map<String, List<DropInfo>> infosByStageId = infos.stream().collect(groupingBy(DropInfo::getStageId));

		infosByStageId.forEach((stageId, infosForOneStage) -> {
			infosForOneStage.sort((info1, info2) -> {
				TimeRange range1 = timeRangeMap.get(info1.getTimeRangeID());
				TimeRange range2 = timeRangeMap.get(info2.getTimeRangeID());
				return range2.getStart().compareTo(range1.getStart());
			});
			String targetTimeRangeID = infosForOneStage.get(0).getTimeRangeID();
			List<DropInfo> filteredInfosForOneStage = infosForOneStage.stream()
					.filter(info -> targetTimeRangeID.equals(info.getTimeRangeID())).collect(toList());
			infosByStageId.put(stageId, filteredInfosForOneStage);
		});
		return infosByStageId;
	}

	/** 
	 * @Title: getDropSetMap 
	 * @Description: Get all dropsets in a map, key is stageId
	 * @param server the server uploader belongs to
	 * @param time current time
	 * @return Map<String,Set<String>>
	 */
	@Override
	public Map<String, Set<String>> getDropSetMap(Server server, Long time) {
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		List<DropInfo> infos = getSpringProxy().getDropInfosByServer(server);

		return infos.stream().filter(info -> {
			String itemId = info.getItemId();
			TimeRange range = timeRangeMap.get(info.getTimeRangeID());
			return itemId != null && range.isIn(time);
		}).collect(groupingBy(DropInfo::getStageId, mapping(DropInfo::getItemId, toSet())));
	}

	/** 
	 * @Title: getDropSet 
	 * @Description: Get all dropped itemIds in a set, under given server, stage and time
	 * @param server the server uploader belongs to
	 * @param stageId the id of stage
	 * @param time current time
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
	 * @param server the server uploader belongs to
	 * @param time current time
	 * @return Map<String,List<DropInfo>>
	 */
	@Override
	public Map<String, List<DropInfo>> getOpeningDropInfosMap(Server server, Long time) {
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		List<DropInfo> infos = getSpringProxy().getDropInfosByServer(server);

		return infos.stream().filter(info -> {
			TimeRange range = timeRangeMap.get(info.getTimeRangeID());
			return range.isIn(time);
		}).collect(groupingBy(DropInfo::getStageId));
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
