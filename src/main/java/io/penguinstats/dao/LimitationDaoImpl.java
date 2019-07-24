package io.penguinstats.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.Limitation;

@Repository(value = "limitationDao")
public class LimitationDaoImpl implements LimitationDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void save(Limitation limitation) {
		mongoTemplate.save(limitation);
	}

	@Override
	public void removeLimitation(String name) {
		Query query = Query.query(Criteria.where("name").is(name));
		mongoTemplate.remove(query, Limitation.class);
	}

	@Override
	public void updateLimitation(Limitation limitation) {
		Query query = new Query(Criteria.where("id").is(limitation.getId()));

		Update update = new Update();
		update.set("name", limitation.getName());
		update.set("itemTypeBounds", limitation.getItemTypeBounds());
		update.set("itemQuantityBounds", limitation.getItemQuantityBounds());
		update.set("inheritance", limitation.getInheritance());

		mongoTemplate.updateFirst(query, update, Limitation.class);
	}

	@Override
	public List<Limitation> findAll() {
		return mongoTemplate.findAll(Limitation.class);
	}

	@Override
	public Limitation findLimitationByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		Limitation limitation = mongoTemplate.findOne(query, Limitation.class);
		return limitation;
	}

}
