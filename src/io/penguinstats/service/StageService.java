package io.penguinstats.service;

import java.util.List;

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

}
