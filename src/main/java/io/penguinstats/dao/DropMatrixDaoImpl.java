package io.penguinstats.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.DropMatrix;

@Repository(value = "dropMatrixDao")
public class DropMatrixDaoImpl implements DropMatrixDao {

	@Resource
	private MongoTemplate mongoTemplate;

	@Override
	public void save(DropMatrix dropMatrix) {
		mongoTemplate.save(dropMatrix);
	}

	@Override
	public void batchSave(List<DropMatrix> list) {
		mongoTemplate.insert(list, DropMatrix.class);
	}

	@Override
	public void removeAll() {
		mongoTemplate.remove(new Query(), DropMatrix.class);
	}

	@Override
	public void removeDropMatrix(String stageId, String itemId) {
		Criteria criteria = new Criteria();
		Query query = new Query(
				criteria.andOperator(Criteria.where("stageId").is(stageId), Criteria.where("itemId").is(itemId)));
		mongoTemplate.remove(query, DropMatrix.class);
	}

	@Override
	public void updateDropMatrix(DropMatrix dropMatrix) {
		Query query = new Query(Criteria.where("id").is(dropMatrix.getId()));

		Update update = new Update();
		update.set("stageId", dropMatrix.getStageId());
		update.set("itemId", dropMatrix.getItemId());
		update.set("quantity", dropMatrix.getQuantity());
		update.set("times", dropMatrix.getTimes());

		mongoTemplate.updateFirst(query, update, DropMatrix.class);
	}

	@Override
	public List<DropMatrix> findAll() {
		//		return mongoTemplate.findAll(DropMatrix.class);
		Query query = new Query();
		return mongoTemplate.find(query, DropMatrix.class);
	}

	@Override
	public DropMatrix findDropMatrixByStageIdAndItemId(String stageId, String itemId) {
		Query query = new Query(
				new Criteria().andOperator(Criteria.where("stageId").is(stageId), Criteria.where("itemId").is(itemId)));
		return mongoTemplate.findOne(query, DropMatrix.class);
	}

	@Override
	public List<DropMatrix> findDropMatrixByStageId(String stageId) {
		Query query = new Query(Criteria.where("stageId").is(stageId));
		return mongoTemplate.find(query, DropMatrix.class);
	}

	@Override
	public void increateTimesForOneStage(String stageId, Integer times) {
		Query query = new Query(Criteria.where("stageId").is(stageId));
		Update update = new Update().inc("times", times);
		mongoTemplate.updateMulti(query, update, DropMatrix.class);
	}

	@Override
	public void increateQuantityForOneElement(String stageId, String itemId, Integer quantity) {
		Query query = new Query(
				new Criteria().andOperator(Criteria.where("stageId").is(stageId), Criteria.where("itemId").is(itemId)));
		Update update = new Update().inc("quantity", quantity);
		mongoTemplate.updateFirst(query, update, DropMatrix.class);
	}

}
