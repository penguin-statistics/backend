package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.addToSet;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

import io.penguinstats.bean.User;

public class UserDao extends BaseDao<User> {

	public UserDao() {
		super("user");
	}

	public User findByUserID(String userID) {
		MongoCursor<Document> iter = collection.find(eq("userID", userID)).iterator();
		if (iter.hasNext()) {
			Document document = iter.next();
			return new User(document);
		}
		return null;
	}

	public void addIP(String userID, String ip) {
		collection.updateOne(eq("userID", userID), addToSet("ips", ip));
	}

}
