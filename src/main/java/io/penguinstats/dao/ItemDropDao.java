package io.penguinstats.dao;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

import io.penguinstats.model.ItemDrop;

public interface ItemDropDao extends BaseDao<ItemDrop> {

	List<ItemDrop> findByIsReliable(Boolean isReliable);

	List<Document> aggregateItemDropQuantities(Criteria criteria);

	List<Document> aggregateStageTimes(Criteria criteria);

}
