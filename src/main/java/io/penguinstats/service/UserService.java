package io.penguinstats.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import io.penguinstats.constant.Constant.CacheValue;
import io.penguinstats.model.User;

public interface UserService {

    @CacheEvict(value = CacheValue.USERS, key = "#user.userID")
    void saveUser(User user);

    @Cacheable(value = CacheValue.USERS, key = "#userID", unless = "#result == null")
    User getUserByUserID(String userID);

    String createNewUser(String ip);

    String createNewUser(String userID, String ip);

    @CacheEvict(value = CacheValue.USERS, key = "#userID")
    void addIP(String userID, String ip);

    @CacheEvict(value = CacheValue.USERS, key = "#userID")
    void addTag(String userID, String tag);

}
