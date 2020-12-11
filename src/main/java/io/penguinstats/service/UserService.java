package io.penguinstats.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import io.penguinstats.model.User;

public interface UserService {

	@CacheEvict(value = "user", key = "'user_' + #user.userID")
	void saveUser(User user);

	@Cacheable(value = "user", key = "'user_' + #userID", unless = "#result == null")
	User getUserByUserID(String userID);

	String createNewUser(String ip);

	String createNewUser(String userID, String ip);

	@CacheEvict(value = "user", key = "'user_' + #userID")
	void addIP(String userID, String ip);

	@CacheEvict(value = "user", key = "'user_' + #userID")
	void addTag(String userID, String tag);

}
