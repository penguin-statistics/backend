package io.penguinstats.dao;

import io.penguinstats.model.Limitation;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LimitationDao extends MongoRepository<Limitation, String> {

	@DeleteQuery("{'name' : ?0 }")
	void removeLimitation(String name);

	Limitation findLimitationByName(String name);
}
