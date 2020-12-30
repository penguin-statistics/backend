package io.penguinstats.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.stereotype.Repository;

import io.penguinstats.enums.SocialPlatform;
import io.penguinstats.model.Authorization;

@Repository
public interface AuthorizationDao extends MongoRepository<Authorization, String> {

	@Query("{'$and' : [{'platform' : ?0}, {'uuid' : ?1}]}")
	Authorization findByPlatformAndUuid(SocialPlatform platform, String uuid);

	@Query("{'$and' : [{'userID' : ?0}, {'platform' : ?1}]}")
	Authorization findByUserIDAndPlatform(String userID, SocialPlatform platform);

	@Query("{'userID' : ?0}")
	List<Authorization> findByUserID(String userID);

	@DeleteQuery("{'$and' : [{'userID' : ?0}, {'platform' : ?1}]}")
	Authorization deleteByUserIDAndPlatform(String userID, SocialPlatform platform);
}
