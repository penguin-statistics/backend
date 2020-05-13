package io.penguinstats.dao;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

import io.penguinstats.model.QueryConditions;

public interface ItemDropDaoCustom {

	List<Document> aggregateItemDrops(QueryConditions conditions);

	List<Document> aggregateItemDropQuantities(Criteria criteria);

	List<Document> aggregateStageTimes(Criteria criteria);

	List<Document> aggregateWeightedItemDropQuantities(Criteria criteria);

	List<Document> aggregateWeightedStageTimes(Criteria criteria);

	List<Document> aggregateUploadCount(Criteria criteria);

}
