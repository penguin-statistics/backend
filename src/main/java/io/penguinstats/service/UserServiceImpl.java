package io.penguinstats.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.UserDao;
import io.penguinstats.enums.ErrorCode;
import io.penguinstats.model.User;
import io.penguinstats.util.exception.ServiceException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final int DIGITS = 8;
    private static final int MAX_RETRY_TIME = 10;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveUser(User user) {
        userDao.save(user);
    }

    @Override
    public User getUserByUserID(String userID) {
        return userDao.findByUserID(userID);
    }

    /**
     * @param ip Initial IP
     * @return String Newly-created userID. If MAX_RETRY_TIME is exceeded, null will be returned and no new user was
     * created.
     * @Title: createNewUser
     * @Description: Create a new user with random userID.
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
        if (times == MAX_RETRY_TIME) {
            log.error("Failed to create new user.");
            throw new ServiceException(ErrorCode.CANNOT_CREATE_USER, "Failed to create new user.", Optional.empty());
        }

        return createNewUser(userID, ip);
    }

    /**
     * @param userID
     * @param ip
     * @return String
     * @Title: createNewUser
     * @Description: Create a new user with indicated userID.
     */
    @Override
    public String createNewUser(String userID, String ip) {
        saveUser(new User(null, userID, 1.0, new ArrayList<>(), ip != null ? Arrays.asList(ip) : new ArrayList<>(),
                null, System.currentTimeMillis(), null, null));
        log.info("new user " + userID + " is created");
        return userID;
    }

    /**
     * @param userID
     * @param ip
     * @return void
     * @Title: addIP
     * @Description: Add IP to ips field of an existing user if IP is not in it.
     */
    @Override
    public void addIP(String userID, String ip) {
        Query query = new Query(Criteria.where("userID").is(userID));
        Update update = new Update();
        update.addToSet("ips", ip);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    /**
     * @param userID
     * @param tag
     * @return void
     * @Title: addTag
     * @Description: Add a tag to tags field of an existing user if IP is not in it.
     */
    @Override
    public void addTag(String userID, String tag) {
        Query query = new Query(Criteria.where("userID").is(userID));
        Update update = new Update();
        update.addToSet("tags", tag);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    /**
     * @return String
     * @Title: generateUserID
     * @Description: Generate a userID. UserID now is a string of 8-digit integer.
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
