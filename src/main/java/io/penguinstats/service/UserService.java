package io.penguinstats.service;

import java.util.Map;

import io.penguinstats.enums.UploadCountType;
import io.penguinstats.model.User;

public interface UserService {

	void saveUser(User user);

	User getUserByUserID(String userID);

	String createNewUser(String ip);

	String createNewUser(String userID, String ip);

	void addIP(String userID, String ip);

	void addTag(String userID, String tag);

	void updateUploadFromMap(Map<String, Integer> map, UploadCountType type);

	void updateWeightByUploadRange(Integer lower, Integer upper, UploadCountType type, Double weight);

}
