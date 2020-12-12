package io.penguinstats.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.penguinstats.enums.Server;
import io.penguinstats.model.PatternMatrixElement;

@Repository
public interface PatternMatrixElementDao extends MongoRepository<PatternMatrixElement, String> {

	Long deleteByServer(Server server);

	List<PatternMatrixElement> findByServer(Server server);

}
