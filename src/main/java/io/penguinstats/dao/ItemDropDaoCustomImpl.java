package io.penguinstats.dao;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;

import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import io.penguinstats.constant.AggregationOperationConstants;
import io.penguinstats.model.ItemDrop;

public class ItemDropDaoCustomImpl implements ItemDropDaoCustom {

	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	AggregationOperationConstants aggregationOperationConstants;

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
		      isReliable:true
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
		List<AggregationOperation> operations = new LinkedList<>();
		operations.add(aggregationOperationConstants.UNWIND_DROPS);
		operations.add(aggregationOperationConstants.GROUP_BY_STAGEID_AND_ITEMID);
		operations.add(aggregationOperationConstants.MATCH_QUANTITY_NOT_ZERO);
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
		List<AggregationOperation> operations = new LinkedList<>();
		operations.add(aggregationOperationConstants.PROJECT_ADD_TIME);
		operations.add(aggregationOperationConstants.UNWIND_ADD_TIME);
		operations.add(aggregationOperationConstants.MATCH_TIMESTAMP);
		operations.add(aggregationOperationConstants.GROUP_BY_STAGEID_AND_POINT);
		operations.add(aggregationOperationConstants.GROUP_BY_STAGEID);
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
		List<AggregationOperation> operations = new LinkedList<>();
		operations.add(aggregationOperationConstants.GROUP_BY_USERID_SUM_TIMES);
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
		List<AggregationOperation> operations = new LinkedList<>();
		operations.add(aggregationOperationConstants.GROUP_BY_USERID_FOR_WEIGHTED_QUANTITIES);
		operations.add(aggregationOperationConstants.LOOKUP_USER);
		operations.add(aggregationOperationConstants.PROJECT_WEIGHT);
		operations.add(aggregationOperationConstants.UNWIND_ITEMDROPS);
		operations.add(aggregationOperationConstants.UNWIND_ITEMDROPS_DROPS);
		operations.add(aggregationOperationConstants.PROJECT_ITEMDROPS);
		operations.add(aggregationOperationConstants.GROUP_BY_STAGEID_AND_ITEMID_SUM_WEIGHTED_QUANTITY);
		operations.add(aggregationOperationConstants.MATCH_QUANTITY_NOT_ZERO);
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
		List<AggregationOperation> operations = new LinkedList<>();
		operations.add(aggregationOperationConstants.GROUP_BY_USERID_FOR_WEIGHTED_STAGE_TIMES);
		operations.add(aggregationOperationConstants.LOOKUP_USER);
		operations.add(aggregationOperationConstants.PROJECT_WEIGHT);
		operations.add(aggregationOperationConstants.UNWIND_ITEMDROPS);
		operations.add(aggregationOperationConstants.PROJECT_ADD_TIME_FOR_WEIGHTED_STAGE_TIMES);
		operations.add(aggregationOperationConstants.UNWIND_ADD_TIME);
		operations.add(aggregationOperationConstants.MATCH_TIMESTAMP_FOR_WEIGHTED_STAGE_TIMES);
		operations.add(aggregationOperationConstants.PROJECT_ITEMDROPS_FOR_WEIGHTED_STAGE_TIMES);
		operations.add(aggregationOperationConstants.GROUP_BY_STAGEID_AND_POINT_SUM_WEIGHTED_TIMES);
		operations.add(aggregationOperationConstants.GROUP_BY_STAGEID);
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
