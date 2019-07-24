package io.penguinstats.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.Stage;

@Repository(value = "stageDao")
public class StageDaoImpl implements StageDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void save(Stage stage) {
		mongoTemplate.save(stage);
	}

	@Override
	public void removeStage(String stageId) {
		Query query = Query.query(Criteria.where("stageId").is(stageId));
		mongoTemplate.remove(query, Stage.class);
	}

	@Override
	public void updateStage(Stage stage) {
		Query query = new Query(Criteria.where("id").is(stage.getId()));

		Update update = new Update();
		update.set("stageType", stage.getStageType());
		update.set("stageId", stage.getStageId());
		update.set("zoneId", stage.getZoneId());
		update.set("code", stage.getCode());
		update.set("apCost", stage.getApCost());
		update.set("normalDrop", stage.getNormalDrop());
		update.set("specialDrop", stage.getSpecialDrop());
		update.set("extraDrop", stage.getExtraDrop());

		mongoTemplate.updateFirst(query, update, Stage.class);
	}

	@Override
	public List<Stage> findAll() {
		return mongoTemplate.findAll(Stage.class);
	}

	@Override
	public Stage findStageByStageId(String stageId) {
		Query query = new Query(Criteria.where("stageId").is(stageId));
		Stage stage = mongoTemplate.findOne(query, Stage.class);
		return stage;
	}

	@Override
	public List<Stage> findStagesByZoneId(String zoneId) {
		Query query = new Query(Criteria.where("zoneId").is(zoneId));
		return mongoTemplate.find(query, Stage.class);
	}

}
