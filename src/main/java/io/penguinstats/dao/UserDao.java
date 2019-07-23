package io.penguinstats.dao;

import io.penguinstats.model.User;

public interface UserDao extends BaseDao<User> {

	void removeUser(String userID);

	void updateUser(User user);

	void addIP(String userID, String ip);

	public void addTag(String userID, String tag);

	User findUserByUserID(String userID);

}
