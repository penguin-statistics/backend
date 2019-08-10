package io.penguinstats.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.ItemDrop;

@Repository
public interface ItemDropDao extends MongoRepository<ItemDrop, String>, ItemDropDaoCustom {

	List<ItemDrop> findByIsReliable(Boolean isReliable);

	@Query("{'$and' : [{'isDeleted' : ?0}, {'userID' : ?1}]}")
	Page<ItemDrop> findByIsDeletedAndUserID(Boolean isDeleted, String userID, Pageable pageable);

	List<ItemDrop> findByUserID(String userID);

}
