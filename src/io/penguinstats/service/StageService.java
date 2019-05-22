package io.penguinstats.service;

import java.util.ArrayList;
import java.util.Arrays;
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

	public boolean savetage(Stage stage) {
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

	public static void main(String[] args) {
		StageService.getInstance().savetage(
				new Stage(52, "2-10", Arrays.asList(30), new ArrayList<>(), Arrays.asList(12, 13, 16, 17, 20, 21), 15));
		StageService.getInstance().savetage(
				new Stage(53, "3-1", Arrays.asList(22), new ArrayList<>(), Arrays.asList(12, 13, 16, 17, 20, 21), 15));
		StageService.getInstance().savetage(
				new Stage(54, "3-2", Arrays.asList(26), new ArrayList<>(), Arrays.asList(0, 1, 4, 5, 8, 9), 15));
		StageService.getInstance().savetage(
				new Stage(55, "3-3", Arrays.asList(28), new ArrayList<>(), Arrays.asList(0, 1, 4, 5, 8, 9), 15));
		StageService.getInstance().savetage(
				new Stage(56, "S3-1", Arrays.asList(13), new ArrayList<>(), Arrays.asList(12, 13, 16, 17, 20, 21), 15));
	}

}
