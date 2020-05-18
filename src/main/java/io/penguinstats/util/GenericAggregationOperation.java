package io.penguinstats.util;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * @author AlvISsReimu
 */
@SuppressWarnings("deprecation")
public class GenericAggregationOperation implements AggregationOperation {

	private String operator;
	private DBObject query;

	/**
	 * Default constructor.
	 * 
	 * @param operator MongoDB operator ($group, $sort, $project, etc..)
	 * @param query MongoDB aggregation query step string
	 */
	public GenericAggregationOperation(String operator, String query) {
		this(operator, (DBObject)JSON.parse(query));
	}

	/**
	 * Default constructor.
	 * 
	 * @param operator MongoDB operator ($group, $sort, $project, etc..)
	 * @param query MongoDB aggregation query step DBObject
	 */
	public GenericAggregationOperation(String operator, DBObject query) {
		this.operator = operator;
		this.query = query;
	}

	@Override
	public Document toDocument(AggregationOperationContext context) {
		return new Document(operator, query);
	}

}
