package io.penguinstats.dao;

import io.penguinstats.model.ItemDrop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemDropDao extends MongoRepository<ItemDrop, String>, ItemDropDaoCustom {

	List<ItemDrop> findByIsReliable(Boolean isReliable);
}
