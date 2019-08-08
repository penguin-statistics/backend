package io.penguinstats.service;

import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import io.penguinstats.enums.UploadCountType;
import io.penguinstats.model.User;

public interface UserService {

	@CacheEvict(value = "user", key = "#user.userID")
	void saveUser(User user);

	@Cacheable(value = "user", key = "#userID", unless = "#result == null")
	User getUserByUserID(String userID);

	String createNewUser(String ip);

	String createNewUser(String userID, String ip);

	@CacheEvict(value = "user", key = "#userID")
	void addIP(String userID, String ip);

	@CacheEvict(value = "user", key = "#userID")
	void addTag(String userID, String tag);

	@CacheEvict(value = "user", allEntries = true)
	void updateUploadFromMap(Map<String, Integer> map, UploadCountType type);

	@CacheEvict(value = "user", allEntries = true)
	void updateWeightByUploadRange(Integer lower, Integer upper, UploadCountType type, Double weight);

}
