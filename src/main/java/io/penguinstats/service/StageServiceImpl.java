package io.penguinstats.service;

import io.penguinstats.enums.ErrorCode;
import io.penguinstats.util.exception.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.dao.StageDao;
import io.penguinstats.model.Stage;
import io.penguinstats.util.LastUpdateTimeUtil;

@Service("stageService")
public class StageServiceImpl implements StageService {

	@Autowired
	private StageDao stageDao;

	@Override
	public void saveStage(Stage stage) {
		stageDao.save(stage);
	}

	@Override
	public Stage getStageByStageId(String stageId) {
		return stageDao.findByStageId(stageId).orElseThrow(
				() -> new NotFoundException(ErrorCode.NOT_FOUND, "Stage[" + stageId + "] is not found",
						Optional.of(stageId)));
	}

	/** 
	 * @Title: getStagesByZoneId 
	 * @Description: Return all stages in the given zone as a list.
	 * @param zoneId
	 * @return List<Stage>
	 */
	@Override
	public List<Stage> getStagesByZoneId(String zoneId) {
		return stageDao.findStagesByZoneId(zoneId);
	}

	/**
	 * @Title: getAllStages
	 * @Description: Return all stages in the database as a list.
	 * @return List<Stage>
	 */
	@Override
	public List<Stage> getAllStages() {
		List<Stage> stages = stageDao.findAll();
		LastUpdateTimeUtil.setCurrentTimestamp(LastUpdateMapKeyName.STAGE_LIST);
		return stages;
	}

	/**
	 * @Title: getStageMap
	 * @Description: Return a map which has stageId as key and stage object as value.
	 * @return Map<String,Stage>
	 */
	@Override
	public Map<String, Stage> getStageMap() {
		List<Stage> list = getAllStages();
		Map<String, Stage> map = new HashMap<>();
		list.forEach(stage -> map.put(stage.getStageId(), stage));
		return map;
	}

}
