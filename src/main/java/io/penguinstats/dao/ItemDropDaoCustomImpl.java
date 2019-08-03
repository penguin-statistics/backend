package io.penguinstats.dao;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LiteralOperators;
import org.springframework.data.mongodb.core.query.Criteria;

import com.mongodb.BasicDBObject;

import io.penguinstats.model.ItemDrop;
import io.penguinstats.util.Constant;

public class ItemDropDaoCustomImpl implements ItemDropDaoCustom {

	@Autowired
	MongoTemplate mongoTemplate;

	/**
	 * @Title: aggregateItemDropQuantities
	 * @Description: Use aggregation to get all item drop quantities under each stage.
	 * @param criteria The filter used in the first 'match' stage.
	 * @return List<Document> Each document contains stageId, itemId and quantity.
	 */
	/** pipeline:
		[  
		  {  
		    $match:{  
		      isReliable:true,
		
		    }
		  },
		  {  
		    $unwind:{  
		      path:"$drops",
		      preserveNullAndEmptyArrays:false
		    }
		  },
		  {  
		    $group:{  
		      _id:{  
		        stageId:"$stageId",
		        itemId:"$drops.itemId"
		      },
		      quantity:{  
		        $sum:"$drops.quantity"
		      }
		    }
		  },
		  {  
		    $project:{  
		      stageId:"$_id.stageId",
		      itemId:"$_id.itemId",
		      quantity:1,
		      _id:0
		    }
		  }
		]
	 */
	@Override
	public List<Document> aggregateItemDropQuantities(Criteria criteria) {
		AggregationOperation unwindDrops = Aggregation.unwind("drops", false);
		AggregationOperation groupByStageIdAndItemId =
				Aggregation.group("stageId", "drops.itemId").sum("drops.quantity").as("quantity");

		List<AggregationOperation> operations = new ArrayList<>();
		operations.add(unwindDrops);
		operations.add(groupByStageIdAndItemId);
		if (criteria != null) {
			AggregationOperation matchGivenCriteria = Aggregation.match(criteria);
			operations.add(0, matchGivenCriteria);
		}

		Aggregation aggregation = newAggregation(operations);

		AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, ItemDrop.class, Document.class);
		return results.getMappedResults();
	}

	/**
	 * @Title: aggregateStageTimes
	 * @Description: Use aggregation to get upload times for each stage under every time point.
	 * @param criteria The filter used in the first 'match' stage.
	 * @returnList<Document> In each document, _id is stageId, and allTimes contain several embedded documents describing stage times.
	 */
	/** pipeline:
		[  
		  {  
		    $match:{  
		      isReliable:true
		    }
		  },
		  {  
		    $addFields:{  
		      addTime:[  
		        0,
		        1558989300000,
		        1560045456000
		      ]
		    }
		  },
		  {  
		    $unwind:{  
		      path:"$addTime",
		      includeArrayIndex:"point",
		      preserveNullAndEmptyArrays:true
		    }
		  },
		  {  
		    $match:{  
		      $expr:{  
		        $gt:[  
		          "$timestamp",
		          "$addTime"
		        ]
		      }
		    }
		  },
		  {  
		    $group:{  
		      _id:{  
		        stageId:"$stageId",
		        point:"$point"
		      },
		      times:{  
		        $sum:"$times"
		      }
		    }
		  },
		  {  
		    $group:{  
		      _id:"$_id.stageId",
		      allTimes:{  
		        $push:{  
		          timePoint:"$_id.point",
		          times:"$times"
		        }
		      }
		    }
		  }
		]
	 */
	@Override
	public List<Document> aggregateStageTimes(Criteria criteria) {
		AggregationOperation projectAddTime = Aggregation.project("stageId", "times", "timestamp")
				.and(LiteralOperators.Literal.asLiteral(Arrays.asList(Constant.ADD_TIME_POINTS))).as("addTime");
		AggregationOperation unwindAddTime = Aggregation.unwind("addTime", "point", true);
		AggregationOperation matchTimestamp = new AggregationOperation() {
			@Override
			public Document toDocument(AggregationOperationContext aoc) {
				return new Document("$match",
						new Document("$expr", new Document("$gt", Arrays.asList("$timestamp", "$addTime"))));
			}
		};
		AggregationOperation groupByStageIdAndPoint = Aggregation.group("stageId", "point").sum("times").as("times");
		AggregationOperation groupByStageId = Aggregation.group("stageId")
				.push(new BasicDBObject("times", "$times").append("timePoint", "$_id.point")).as("allTimes");

		List<AggregationOperation> operations = new ArrayList<>();
		operations.add(projectAddTime);
		operations.add(unwindAddTime);
		operations.add(matchTimestamp);
		operations.add(groupByStageIdAndPoint);
		operations.add(groupByStageId);
		if (criteria != null) {
			AggregationOperation matchGivenCriteria = Aggregation.match(criteria);
			operations.add(0, matchGivenCriteria);
		}
		Aggregation aggregation = newAggregation(operations);

		AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, ItemDrop.class, Document.class);
		return results.getMappedResults();
	}

	/**
	 * @Title: aggregateUploadCount
	 * @Description: Use aggregation to get upload count of a user
	 * @param criteria The filter used in the first 'match' stage.
	 * @returnList<Document> In each document, _id is userID, and count is the total upload count.
	 */
	@Override
	public List<Document> aggregateUploadCount(Criteria criteria) {
		AggregationOperation groupByUserID = Aggregation.group("userID").sum("times").as("count");
		List<AggregationOperation> operations = new ArrayList<>();
		operations.add(groupByUserID);
		if (criteria != null) {
			AggregationOperation matchGivenCriteria = Aggregation.match(criteria);
			operations.add(0, matchGivenCriteria);
		}
		Aggregation aggregation = newAggregation(operations);

		AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, ItemDrop.class, Document.class);
		return results.getMappedResults();
	}

}
