package io.penguinstats.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.SystemProperty;

@Repository
public interface SystemPropertyDao extends MongoRepository<SystemProperty, String> {

	SystemProperty findByKey(String key);

}
