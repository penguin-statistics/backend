package io.penguinstats.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.Item;

@Repository
public interface ItemDao extends MongoRepository<Item, String> {

	Item findByItemId(String itemId);

}
