package io.penguinstats.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;

@Repository
public interface DropInfoDao extends MongoRepository<DropInfo, String> {

	List<DropInfo> findDropInfosByServer(Server server);

	@Query("{'$and' : [{'server' : ?0}, {'stageId' : ?1}]}")
	List<DropInfo> findDropInfosByServerAndStageId(Server server, String stageId);

	@Query("{'$and' : [{'server' : ?0}, {'timeRangeID' : ?1}]}")
	List<DropInfo> findDropInfosByServerAndTimeRangeID(Server server, String timeRangeID);

}
