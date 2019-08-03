package io.penguinstats.dao;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

public interface ItemDropDaoCustom {

	List<Document> aggregateItemDropQuantities(Criteria criteria);

	List<Document> aggregateStageTimes(Criteria criteria);

	List<Document> aggregateUploadCount(Criteria criteria);

}
