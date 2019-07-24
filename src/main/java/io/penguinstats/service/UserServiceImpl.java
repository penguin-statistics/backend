package io.penguinstats.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.UserDao;
import io.penguinstats.model.User;

@Service("userService")
public class UserServiceImpl implements UserService {

	private static final int DIGITS = 8;
	private static final int MAX_RETRY_TIME = 10;

	private static Logger logger = LogManager.getLogger(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Override
	public void saveUser(User user) {
		userDao.save(user);
	}

	@Override
	public User getUserByUserID(String userID) {
		return userDao.findUserByUserID(userID);
	}

	/**
	 * @Title: createNewUser
	 * @Description: Create a new user with random userID.
	 * @param ip Initial IP
	 * @return String Newly-created userID. If MAX_RETRY_TIME is exceeded, null will be returned and no new user was
	 *         created.
	 */
	@Override
	public String createNewUser(String ip) {
		String userID = generateUserID();
		int times = 0;
		while (times < MAX_RETRY_TIME) {
			userID = generateUserID();
			if (getUserByUserID(userID) == null)
				break;
			times++;
		}
		if (times == MAX_RETRY_TIME)
			return null;
		return createNewUser(userID, ip);
	}

	/** 
	 * @Title: createNewUser 
	 * @Description: Create a new user with indicated userID.
	 * @param userID
	 * @param ip
	 * @return String
	 */
	@Override
	public String createNewUser(String userID, String ip) {
		saveUser(new User(userID, 1.0, new ArrayList<>(), ip != null ? Arrays.asList(ip) : new ArrayList<>(), null,
				System.currentTimeMillis()));
		logger.info("new user " + userID + " is created");
		return userID;
	}

	/**
	 * @Title: addIP
	 * @Description: Add IP to ips field of an existing user if IP is not in it.
	 * @param userID
	 * @param ip
	 * @return void
	 */
	@Override
	public void addIP(String userID, String ip) {
		userDao.addIP(userID, ip);
	}

	/** 
	 * @Title: addTag 
	 * @Description: Add a tag to tags field of an existing user if IP is not in it.
	 * @param userID
	 * @param tag
	 * @return void
	 */
	@Override
	public void addTag(String userID, String tag) {
		userDao.addTag(userID, tag);
	}

	/**
	 * @Title: generateUserID
	 * @Description: Generate a userID. UserID now is a string of 8-digit integer.
	 * @return String
	 */
	private String generateUserID() {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < DIGITS; i++) {
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}

}
