package io.penguinstats.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.penguinstats.bean.Stage;
import io.penguinstats.dao.StageDao;

public class StageService {

	private static StageService instance = new StageService();
	private static StageDao dao = new StageDao();

	private StageService() {}

	public static StageService getInstance() {
		return instance;
	}

	public boolean saveStage(Stage stage) {
		return dao.save(stage);
	}

	public Stage getStage(int id) {
		return dao.findByID(id);
	}

	public List<Stage> getAllStages() {
		return dao.findAll();
	}

	public Map<Integer, Stage> getStageMap() {
		List<Stage> list = getAllStages();
		Map<Integer, Stage> map = new HashMap<>();
		list.forEach(stage -> map.put(stage.getId(), stage));
		return map;
	}

}
