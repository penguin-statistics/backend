package io.penguinstats.dao;

import java.util.List;

import org.bson.Document;

import io.penguinstats.model.QueryConditions;

public interface ItemDropDaoCustom {

	List<Document> aggregateItemDrops(QueryConditions conditions);

	List<Document> aggregateStageTimes(QueryConditions conditions);

	List<Document> aggregateItemQuantities(QueryConditions conditions);

}
