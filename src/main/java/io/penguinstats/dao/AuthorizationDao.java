package io.penguinstats.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import io.penguinstats.enums.SocialPlatform;
import io.penguinstats.model.Authorization;

@Repository
public interface AuthorizationDao extends MongoRepository<Authorization, String> {

	@Query("{'$and' : [{'userID' : ?0}, {'platform' : ?1}]}")
	Authorization findByUserIDAndPlatform(String userID, SocialPlatform platform);

	@Query("{'$and' : [{'state' : ?0}, {'platform' : ?1}]}")
	Authorization findByStateAndPlatform(String state, SocialPlatform platform);

}
