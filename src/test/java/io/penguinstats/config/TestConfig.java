package io.penguinstats.config;

import io.penguinstats.model.User;
import io.penguinstats.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@TestConfiguration
public class TestConfig {

    public static MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    @Autowired
    UserService userService;

    public static String testUserID = "spring-test";

    @PostConstruct
    public void init() {preload();}

    private void preload() {
        User user = new User(testUserID, 1.0, Arrays.asList("test"), Arrays.asList("0.0.0.0"), null, null);
        userService.saveUser(user);
    }
}
