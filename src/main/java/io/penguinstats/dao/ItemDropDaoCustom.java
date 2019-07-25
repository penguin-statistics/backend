package io.penguinstats.dao;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public interface ItemDropDaoCustom {

    List<Document> aggregateItemDropQuantities(Criteria criteria);

    List<Document> aggregateStageTimes(Criteria criteria);
}
