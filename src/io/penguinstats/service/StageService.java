package io.penguinstats.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.penguinstats.bean.Stage;
import io.penguinstats.dao.StageDao;

public class StageService {

	private static StageService instance = new StageService();
	private static StageDao stageDao = new StageDao();

	private StageService() {}

	public static StageService getInstance() {
		return instance;
	}

	public boolean saveStage(Stage stage) {
		return stageDao.save(stage);
	}

	public Stage getStage(String stageId) {
		return stageDao.findByStageId(stageId);
	}

	/**
	 * @Title: getAllStages
	 * @Description: Return all stages in the database as a list.
	 * @return List<Stage>
	 */
	public List<Stage> getAllStages() {
		return stageDao.findAll();
	}

	/**
	 * @Title: getStageMap
	 * @Description: Return a map which has stageId as key and stage object as value.
	 * @return Map<String,Stage>
	 */
	public Map<String, Stage> getStageMap() {
		List<Stage> list = getAllStages();
		Map<String, Stage> map = new HashMap<>();
		list.forEach(stage -> map.put(stage.getStageId(), stage));
		return map;
	}

}
