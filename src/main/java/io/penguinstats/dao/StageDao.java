package io.penguinstats.dao;

import java.util.List;

import io.penguinstats.model.Stage;

public interface StageDao extends BaseDao<Stage> {

	void removeStage(String stageId);

	void updateStage(Stage stage);

	Stage findStageByStageId(String stageId);

	List<Stage> findStagesByZoneId(String zoneId);

}
