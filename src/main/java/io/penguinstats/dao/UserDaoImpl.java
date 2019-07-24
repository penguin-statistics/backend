package io.penguinstats.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.User;

@Repository(value = "userDao")
public class UserDaoImpl implements UserDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void save(User user) {
		mongoTemplate.save(user);
	}

	@Override
	public void removeUser(String userID) {
		Query query = Query.query(Criteria.where("userID").is(userID));
		mongoTemplate.remove(query, User.class);
	}

	@Override
	public void updateUser(User user) {
		Query query = new Query(Criteria.where("id").is(user.getId()));

		Update update = new Update();
		update.set("userID", user.getUserID());
		update.set("weight", user.getWeight());
		update.set("tags", user.getTags());
		update.set("ips", user.getIps());
		update.set("comment", user.getComment());
		update.set("createTime", user.getCreateTime());

		mongoTemplate.updateFirst(query, update, User.class);
	}

	@Override
	public void addIP(String userID, String ip) {
		Query query = new Query(Criteria.where("userID").is(userID));
		Update update = new Update();
		update.addToSet("ips", ip);
		mongoTemplate.updateFirst(query, update, User.class);
	}

	@Override
	public void addTag(String userID, String tag) {
		Query query = new Query(Criteria.where("userID").is(userID));
		Update update = new Update();
		update.addToSet("tags", tag);
		mongoTemplate.updateFirst(query, update, User.class);
	}

	@Override
	public List<User> findAll() {
		return mongoTemplate.findAll(User.class);
	}

	@Override
	public User findUserByUserID(String userID) {
		Query query = new Query(Criteria.where("userID").is(userID));
		User user = mongoTemplate.findOne(query, User.class);
		return user;
	}

}
