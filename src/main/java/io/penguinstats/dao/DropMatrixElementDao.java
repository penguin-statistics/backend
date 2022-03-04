package io.penguinstats.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import io.penguinstats.enums.DropMatrixElementType;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropMatrixElement;

@Repository
public interface DropMatrixElementDao extends MongoRepository<DropMatrixElement, String> {

    @DeleteQuery("{'$and' : [{'type' : ?0}, {'isPast' : ?1}, {'server' : ?2}]}")
    Long deleteAllByIsPastAndServer(DropMatrixElementType type, Boolean isPast, Server server);

    @DeleteQuery("{'$and' : [{'type' : ?0}, {'isPast' : ?1}, {'server' : ?2}, {'stageId' : ?3}]}")
    Long deleteAllByIsPastAndServerAndStageId(DropMatrixElementType type, Boolean isPast, Server server,
            String stageId);

    @Query("{'$and' : [{'type' : ?0}, {'isPast' : ?1}, {'server' : ?2}]}")
    List<DropMatrixElement> queryByTypeAndIsPastAndServer(DropMatrixElementType type, Boolean isPast, Server server);

}
