package io.penguinstats.dao;

import io.penguinstats.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDao extends MongoRepository<Item, String> {

	Item findItemByItemId(String itemId);

}
