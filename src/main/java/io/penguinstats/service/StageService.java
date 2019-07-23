package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import io.penguinstats.model.Stage;

public interface StageService {

	void saveStage(Stage stage);

	Stage getStageByStageId(String stageId);

	List<Stage> getStagesByZoneId(String zoneId);

	List<Stage> getAllStages();

	Map<String, Stage> getStageMap();

}
