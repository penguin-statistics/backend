package io.penguinstats.dao;

import io.penguinstats.model.DropMatrix;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DropMatrixDao extends MongoRepository<DropMatrix, String> {

	@DeleteQuery("{'$and' : [{'stageId' : ?0}, {'itemId' : ?1}]}")
	void removeDropMatrix(String stageId, String itemId);

	@Query("{'$and' : [{'stageId' : ?0}, {'itemId' : ?1}]}")
	DropMatrix findDropMatrixByStageIdAndItemId(String stageId, String itemId);

	List<DropMatrix> findDropMatrixByStageId(String stageId);

}
