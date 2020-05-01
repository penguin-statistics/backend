package io.penguinstats.dao;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.Limitation;

@Repository
public interface LimitationDao extends MongoRepository<Limitation, String> {

	@DeleteQuery("{'name' : ?0 }")
	void removeLimitation(String name);

}
