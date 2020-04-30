package io.penguinstats.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.Limitation;

@Repository
public interface LimitationDao extends MongoRepository<Limitation, String> {

	List<Limitation> findLimitationsByStageId(String stageId);

}
