package io.penguinstats.dao;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
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

		List<AggregationOperation> operations = new LinkedList<>();
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

		List<AggregationOperation> operations = new LinkedList<>();
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
		List<AggregationOperation> operations = new LinkedList<>();
		operations.add(groupByUserID);
		if (criteria != null) {
			AggregationOperation matchGivenCriteria = Aggregation.match(criteria);
			operations.add(0, matchGivenCriteria);
		}
		Aggregation aggregation = newAggregation(operations);

		AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, ItemDrop.class, Document.class);
		return results.getMappedResults();
	}

	/**
	 * @Title: aggregateWeightedItemDropQuantities
	 * @Description: Use aggregation to get all weighted item drop quantities under each stage.
	 * @param criteria The filter used in the first 'match' stage.
	 * @return List<Document> Each document contains stageId, itemId and quantity.
	 */
	/** pipeline:
		[{
		    $match: {
		        isReliable: true
		    }
		}, {
		    $group: {
		        _id: "$userID",
		        itemDrops: {
		            $push: {
		                drops: "$$ROOT.drops",
		                stageId: "$$ROOT.stageId"
		            }
		        }
		    }
		}, {
		    $lookup: {
		        from: 'user',
		        localField: '_id',
		        foreignField: 'userID',
		        as: 'user'
		    }
		}, {
		    $project: {
		        _id: 0,
		        itemDrops: 1,
		        weight: {
		            $arrayElemAt: ["$user.weight", 0]
		        }
		    }
		}, {
		    $unwind: {
		        path: "$itemDrops",
		        preserveNullAndEmptyArrays: false
		    }
		}, {
		    $unwind: {
		        path: "$itemDrops.drops",
		        preserveNullAndEmptyArrays: false
		    }
		}, {
		    $group: {
		        _id: {
		            stageId: '$itemDrops.stageId',
		            itemId: '$itemDrops.drops.itemId'
		        },
		        weightedQuantity: {
		            $sum: {
		                $multiply: [
		                    "$weight",
		                    "$itemDrops.drops.quantity"
		                ]
		            }
		        }
		    }
		}, {
		    $project: {
		        stageId: '$_id.stageId',
		        itemId: '$_id.itemId',
		        weightedQuantity: 1,
		        _id: 0
		    }
		}]
	 */
	@Override
	public List<Document> aggregateWeightedItemDropQuantities(Criteria criteria) {
		AggregationOperation groupByUserID = Aggregation.group("userID")
				.push(new BasicDBObject("drops", "$$ROOT.drops").append("stageId", "$$ROOT.stageId")).as("itemDrops");
		AggregationOperation lookUpUser = Aggregation.lookup("user", "_id", "userID", "user");
		AggregationOperation projectWeight = Aggregation.project("itemDrops")
				.and(ArrayOperators.ArrayElemAt.arrayOf("user.weight").elementAt(0)).as("weight");
		AggregationOperation unwindItemDrops = Aggregation.unwind("itemDrops", false);
		AggregationOperation unwindDrops = Aggregation.unwind("itemDrops.drops", false);
		AggregationOperation projectItemDrops =
				Aggregation.project("weight").and("itemDrops.stageId").as("stageId").and("itemDrops.drops").as("drops");
		AggregationOperation groupByStageIdAndItemId = Aggregation.group("stageId", "drops.itemId")
				.sum(ArithmeticOperators.Multiply.valueOf("weight").multiplyBy("drops.quantity")).as("quantity");

		List<AggregationOperation> operations = new LinkedList<>();
		operations.add(groupByUserID);
		operations.add(lookUpUser);
		operations.add(projectWeight);
		operations.add(unwindItemDrops);
		operations.add(unwindDrops);
		operations.add(projectItemDrops);
		operations.add(groupByStageIdAndItemId);
		if (criteria != null) {
			AggregationOperation matchGivenCriteria = Aggregation.match(criteria);
			operations.add(0, matchGivenCriteria);
		}

		Aggregation aggregation =
				newAggregation(operations).withOptions(newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, ItemDrop.class, Document.class);
		return results.getMappedResults();
	}

	/**
	 * @Title: aggregateWeightedStageTimes
	 * @Description: Use aggregation to get weighted upload times for each stage under every time point.
	 * @param criteria The filter used in the first 'match' stage.
	 * @returnList<Document> In each document, _id is stageId, and allTimes contain several embedded documents describing stage times.
	 */
	/** pipeline:
		[{
		    $match: {
		        isReliable: true
		    }
		}, {
		    $group: {
		        _id: "$userID",
		        itemDrops: {
		            $push: {
		                stageId: "$$ROOT.stageId",
		                times: "$$ROOT.times",
		                timestamp: "$$ROOT.timestamp"
		            }
		        }
		    }
		}, {
		    $lookup: {
		        from: 'user',
		        localField: '_id',
		        foreignField: 'userID',
		        as: 'user'
		    }
		}, {
		    $project: {
		        _id: 0,
		        itemDrops: 1,
		        weight: {
		            $arrayElemAt: ["$user.weight", 0]
		        }
		    }
		}, {
		    $unwind: {
		        path: "$itemDrops",
		        preserveNullAndEmptyArrays: false
		    }
		}, {
		    $addFields: {
		        addTime: [0, 1558989300000, 1560045456000]
		    }
		}, {
		    $unwind: {
		        path: "$addTime",
		        includeArrayIndex: "point",
		        preserveNullAndEmptyArrays: true
		    }
		}, {
		    $match: {
		        $expr: {
		            $gt: ["$itemDrops.timestamp", "$addTime"]
		        }
		    }
		}, {
		    $group: {
		        _id: {
		            stageId: "$itemDrops.stageId",
		            point: "$point"
		        },
		        weightedTimes: {
		            $sum: {
		                $multiply: [
		                    "$itemDrops.times",
		                    "$weight"
		                ]
		            }
		        }
		    }
		}, {
		    $group: {
		        _id: "$_id.stageId",
		        allWeightedTimes: {
		            $push: {
		                timePoint: "$_id.point",
		                times: "$times"
		            }
		        }
		    }
		}]
	 */
	@Override
	public List<Document> aggregateWeightedStageTimes(Criteria criteria) {
		AggregationOperation groupByUserID =
				Aggregation
						.group("userID").push(new BasicDBObject("times", "$$ROOT.times")
								.append("stageId", "$$ROOT.stageId").append("timestamp", "$$ROOT.timestamp"))
						.as("itemDrops");
		AggregationOperation lookUpUser = Aggregation.lookup("user", "_id", "userID", "user");
		AggregationOperation projectWeight = Aggregation.project("itemDrops")
				.and(ArrayOperators.ArrayElemAt.arrayOf("user.weight").elementAt(0)).as("weight");
		AggregationOperation unwindItemDrops = Aggregation.unwind("itemDrops", false);
		AggregationOperation projectAddTime = Aggregation.project("itemDrops", "weight")
				.and(LiteralOperators.Literal.asLiteral(Arrays.asList(Constant.ADD_TIME_POINTS))).as("addTime");
		AggregationOperation unwindAddTime = Aggregation.unwind("addTime", "point", true);
		AggregationOperation matchTimestamp = new AggregationOperation() {
			@Override
			public Document toDocument(AggregationOperationContext aoc) {
				return new Document("$match",
						new Document("$expr", new Document("$gt", Arrays.asList("$itemDrops.timestamp", "$addTime"))));
			}
		};
		AggregationOperation projectItemDrops = Aggregation.project("weight", "point").and("itemDrops.stageId")
				.as("stageId").and("itemDrops.times").as("times");
		AggregationOperation groupByStageIdAndPoint = Aggregation.group("stageId", "point")
				.sum(ArithmeticOperators.Multiply.valueOf("weight").multiplyBy("times")).as("weightedTimes");
		AggregationOperation groupByStageId = Aggregation.group("stageId")
				.push(new BasicDBObject("times", "$weightedTimes").append("timePoint", "$_id.point")).as("allTimes");

		List<AggregationOperation> operations = new LinkedList<>();
		operations.add(groupByUserID);
		operations.add(lookUpUser);
		operations.add(projectWeight);
		operations.add(unwindItemDrops);
		operations.add(projectAddTime);
		operations.add(unwindAddTime);
		operations.add(matchTimestamp);
		operations.add(projectItemDrops);
		operations.add(groupByStageIdAndPoint);
		operations.add(groupByStageId);
		if (criteria != null) {
			AggregationOperation matchGivenCriteria = Aggregation.match(criteria);
			operations.add(0, matchGivenCriteria);
		}
		Aggregation aggregation =
				newAggregation(operations).withOptions(newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, ItemDrop.class, Document.class);
		return results.getMappedResults();
	}

}
