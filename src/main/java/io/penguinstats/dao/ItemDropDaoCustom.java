package io.penguinstats.dao;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

public interface ItemDropDaoCustom {

	List<Document> aggregateItemDropQuantities(Criteria criteria);

	List<Document> aggregateStageTimes(Criteria criteria);

	List<Document> aggregateWeightedItemDropQuantities(Criteria criteria);

	List<Document> aggregateWeightedStageTimes(Criteria criteria);

	List<Document> aggregateSegmentedWeightedItemDropQuantities(Criteria criteria, String stageId, long startTime,
			long interval, String itemId);

	List<Document> aggregateSegmentedWeightedStageTimes(Criteria criteria, String stageId, long startTime,
			long interval);

	List<Document> aggregateUploadCount(Criteria criteria);

	Long findMinTimestamp(Boolean isReliable, Boolean isDeleted, String stageId);

}
