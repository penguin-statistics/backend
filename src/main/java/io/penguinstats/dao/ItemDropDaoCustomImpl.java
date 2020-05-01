package io.penguinstats.dao;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.LiteralOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import io.penguinstats.constant.AggregationOperationConstants;
import io.penguinstats.enums.Server;
import io.penguinstats.model.ItemDrop;
import io.penguinstats.model.QueryConditions;
import io.penguinstats.model.QueryConditions.StageWithTimeRange;

public class ItemDropDaoCustomImpl implements ItemDropDaoCustom {

	private static Logger logger = LogManager.getLogger(ItemDropDaoCustomImpl.class);

	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	AggregationOperationConstants aggregationOperationConstants;

	@Override
	public List<Document> aggregateItemDropQuantities(QueryConditions conditions) {
		Long currentTime = System.currentTimeMillis();

		List<String> userIDs = conditions.getUserIDs();
		List<String> itemIds = conditions.getItemIds();
		List<Server> servers = conditions.getServers();
		List<StageWithTimeRange> stages = conditions.getStages();
		Long interval = conditions.getInterval();

		List<AggregationOperation> operations = new LinkedList<>();

		// Pipe 1: filter by isReliable, isDeleted, stageId and timestamp
		List<Criteria> criteriasInAndInPipe1 = new ArrayList<>();

		criteriasInAndInPipe1.add(Criteria.where("isReliable").is(true));

		if (userIDs.isEmpty())
			criteriasInAndInPipe1.add(Criteria.where("isDeleted").is(false));
		else
			criteriasInAndInPipe1.add(Criteria.where("userID").in(userIDs));

		if (!servers.isEmpty())
			criteriasInAndInPipe1.add(Criteria.where("server").in(servers));

		if (!stages.isEmpty()) {
			if (1 == stages.size() && stages.get(0).getStageId() == null) {
				StageWithTimeRange stage = stages.get(0);
				Long min = stage.getStart() == null ? 0L : stage.getStart();
				Long max = stage.getEnd() == null ? System.currentTimeMillis() : stage.getEnd();
				criteriasInAndInPipe1.add(Criteria.where("timestamp").gte(min).lt(max));
			} else {
				List<Criteria> criteriasInOrInPipe1 = new ArrayList<>();
				stages.forEach(stage -> {
					Long min = stage.getStart() == null ? 0L : stage.getStart();
					Long max = stage.getEnd() == null ? System.currentTimeMillis() : stage.getEnd();
					criteriasInOrInPipe1.add(new Criteria().andOperator(Criteria.where("timestamp").gte(min).lt(max),
							Criteria.where("stageId").is(stage.getStageId())));
				});
				criteriasInAndInPipe1.add(new Criteria().orOperator(criteriasInOrInPipe1.toArray(new Criteria[0])));
			}
		}

		operations.add(Aggregation.match(new Criteria().andOperator(criteriasInAndInPipe1.toArray(new Criteria[0]))));

		// Pipe 2: project section number
		if (interval != null) {
			Long baseTime = null;
			if (stages.isEmpty())
				baseTime = 0L;
			else {
				final Long firstStartTime = stages.get(0).getStart();
				boolean passCheck = true;
				for (int i = 1, l = stages.size(); i < l; i++) {
					StageWithTimeRange stage = stages.get(i);
					if (!stage.getStart().equals(firstStartTime)) {
						logger.error("start time must be identical for all stages in the conditions");
						passCheck = false;
						break;
					}
				}
				if (passCheck)
					baseTime = firstStartTime == null ? 0L : firstStartTime;
			}

			operations.add(Aggregation.project("drops", "stageId", "times")
					.and(ArithmeticOperators.Trunc.truncValueOf(ArithmeticOperators.Divide
							.valueOf(ArithmeticOperators.Subtract.valueOf("timestamp").subtract(baseTime))
							.divideBy(interval)))
					.as("section"));
		} else {
			operations.add(Aggregation.project("drops", "stageId", "times")
					.and(LiteralOperators.Literal.asLiteral(0.0d)).as("section"));
		}

		// Pipe 3: group by section and stageId, calculate times
		operations
				.add(Aggregation.group("section", "stageId").push("$$ROOT.drops").as("drops").sum("times").as("times"));

		// Pipe 4 & 5: unwind drops
		operations.add(Aggregation.unwind("drops", false));
		operations.add(Aggregation.unwind("drops", false));

		// Pipe 6: filter on itemId
		if (!itemIds.isEmpty())
			operations.add(Aggregation.match(Criteria.where("drops.itemId").in(itemIds)));

		// Pipe 7: project and group by itemId and calculate quantities
		operations.add(Aggregation.project("section", "stageId", "times").and("drops.itemId").as("itemId")
				.and("drops.quantity").as("quantity"));
		operations.add(Aggregation.group("section", "stageId", "times", "itemId").sum("quantity").as("quantity"));

		Aggregation aggregation =
				newAggregation(operations).withOptions(newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, ItemDrop.class, Document.class);

		logger.info(conditions.toString() + ", time = " + (System.currentTimeMillis() - currentTime) + "ms");

		return results.getMappedResults();
	}

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
		if (criteria == null)
			operations.add(aggregationOperationConstants.MATCH_RELIEABLE_NOT_DELETED);
		else
			operations.add(aggregationOperationConstants.MATCH_NOT_DELETED);
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
		[{
		    $match: {
		        isReliable: true
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
		    $project: {
		        stageId: 1,
		        point: 1,
		        times: {
		            $cond: {
		                if: {
		                    $gt: ["$timestamp", "$addTime"]
		                },
		                then: $times,
		                else: 0
		            }
		        }
		    }
		}, {
		    $group: {
		        _id: {
		            stageId: "$stageId",
		            point: "$point"
		        },
		        times: {
		            $sum: "$times"
		        }
		    }
		}, {
		    $group: {
		        _id: "$_id.stageId",
		        allTimes: {
		            $push: {
		                timePoint: "$_id.point",
		                times: "$times"
		            }
		        }
		    }
		}]
	 */
	@Override
	public List<Document> aggregateStageTimes(Criteria criteria) {
		List<AggregationOperation> operations = new LinkedList<>();
		if (criteria == null)
			operations.add(aggregationOperationConstants.MATCH_RELIEABLE_NOT_DELETED);
		else
			operations.add(aggregationOperationConstants.MATCH_NOT_DELETED);
		operations.add(aggregationOperationConstants.PROJECT_ADD_TIME);
		operations.add(aggregationOperationConstants.UNWIND_ADD_TIME);
		operations.add(aggregationOperationConstants.PROJECT_TIMESTAMP_GT_ADD_TIME);
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
		        quantity: {
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
		        quantity: 1,
		        _id: 0
		    }
		}]
	 */
	@Override
	public List<Document> aggregateWeightedItemDropQuantities(Criteria criteria) {
		List<AggregationOperation> operations = new LinkedList<>();
		operations.add(aggregationOperationConstants.MATCH_RELIEABLE_NOT_DELETED);
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
		    $project: {
		        stageId: "$itemDrops.stageId",
		        point: 1,
		        weight: 1,
		        times: {
		            $cond: {
		                if: {
		                    $gt: ["$itemDrops.timestamp", "$addTime"]
		                },
		                then: 1,
		                else: 0
		            }
		        }
		    }
		}, {
		    $group: {
		        _id: {
		            stageId: "$stageId",
		            point: "$point"
		        },
		        times: {
		            $sum: {
		                $multiply: [
		                    "$times",
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
		operations.add(aggregationOperationConstants.MATCH_RELIEABLE_NOT_DELETED);
		operations.add(aggregationOperationConstants.GROUP_BY_USERID_FOR_WEIGHTED_STAGE_TIMES);
		operations.add(aggregationOperationConstants.LOOKUP_USER);
		operations.add(aggregationOperationConstants.PROJECT_WEIGHT);
		operations.add(aggregationOperationConstants.UNWIND_ITEMDROPS);
		operations.add(aggregationOperationConstants.PROJECT_ADD_TIME_FOR_WEIGHTED_STAGE_TIMES);
		operations.add(aggregationOperationConstants.UNWIND_ADD_TIME);
		operations.add(aggregationOperationConstants.PROJECT_TIMESTAMP_GT_ADD_TIME_FOR_WEIGHTED_STAGE_TIMES);
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

	/** 
	 * @Title: aggregateSegmentedWeightedItemDropQuantities 
	 * @Description: Use aggregation to get all weighted item drop quantities under each stage for every interval.
	 * @param criteria The filter used in the first 'match' stage.
	 * @param stageId Required.
	 * @param startTime The earliest upload time of this stage.
	 * @param interval
	 * @param itemId Optional.
	 * @return List<Document> Each document contains section, quantity, and itemId if not provided as parameter.
	 */
	/** pipeline:
		[{
		    $match: {
		        stageId: 'main_01-07'
		    }
		}, {
		    $project: {
		        _id: 0,
		        drops: 1,
		        userID: 1,
		        section: {
		            $trunc: {
		                $divide: [{
		                        $subtract: [
		                            '$timestamp',
		                            1558530453551
		                        ]
		                    },
		                    86400000
		                ]
		            }
		        }
		    }
		}, {
		    $group: {
		        _id: "$section",
		        itemDrops: {
		            $push: {
		                itemDrop: '$$ROOT'
		            }
		        }
		    }
		}, {
		    $project: {
		        _id: 0,
		        section: "$_id",
		        itemDrops: 1
		    }
		}, {
		    $unwind: {
		        path: "$itemDrops",
		        preserveNullAndEmptyArrays: false
		    }
		}, {
		    $project: {
		        drops: "$itemDrops.itemDrop.drops",
		        userID: "$itemDrops.itemDrop.userID",
		        section: "$section"
		    }
		}, {
		    $group: {
		        _id: "$userID",
		        itemDrops: {
		            $push: {
		                section: "$$ROOT.section",
		                drops: "$$ROOT.drops"
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
		            $arrayElemAt: [
		                '$user.weight',
		                0
		            ]
		        }
		    }
		}, {
		    $unwind: {
		        path: '$itemDrops',
		        preserveNullAndEmptyArrays: false
		    }
		}, {
		    $unwind: {
		        path: '$itemDrops.drops',
		        preserveNullAndEmptyArrays: false
		    }
		}, {
		    $group: {
		        _id: {
		            section: '$itemDrops.section',
		            itemId: '$itemDrops.drops.itemId'
		        },
		        quantity: {
		            $sum: {
		                $multiply: [
		                    '$weight',
		                    '$itemDrops.drops.quantity'
		                ]
		            }
		        }
		    }
		}]
	 */
	@Override
	public List<Document> aggregateSegmentedWeightedItemDropQuantities(Criteria criteria, String stageId,
			long startTime, long interval, String itemId) {
		if (stageId == null || startTime < 0 || interval < 0)
			return null;
		List<AggregationOperation> operations = new LinkedList<>();
		operations.add(aggregationOperationConstants.MATCH_RELIEABLE_NOT_DELETED);
		operations.add(Aggregation.match(Criteria.where("stageId").is(stageId)));
		operations.add(Aggregation.project("drops", "userID")
				.and(ArithmeticOperators.Trunc.truncValueOf(ArithmeticOperators.Divide
						.valueOf(ArithmeticOperators.Subtract.valueOf("timestamp").subtract(startTime))
						.divideBy(interval)))
				.as("section"));
		operations.add(aggregationOperationConstants.GROUP_BY_SECTION);
		operations.add(aggregationOperationConstants.UNWIND_ITEMDROPS);
		operations.add(aggregationOperationConstants.PROJECT_SECTION);
		operations.add(aggregationOperationConstants.GROUP_BY_USERID_FOR_SEGMENTED_WEIGHTED_QUANTITIES);
		operations.add(aggregationOperationConstants.LOOKUP_USER);
		operations.add(aggregationOperationConstants.PROJECT_WEIGHT);
		operations.add(aggregationOperationConstants.UNWIND_ITEMDROPS);
		operations.add(aggregationOperationConstants.UNWIND_ITEMDROPS_DROPS);
		if (itemId == null) {
			operations.add(aggregationOperationConstants.PROJECT_ITEMDROPS_FOR_SEGMENTED_WEIGHTED_QUANTITIES);
			operations.add(aggregationOperationConstants.GROUP_BY_SECTION_AND_ITEMID_SUM_WEIGHTED_QUANTITY);
		} else {
			operations
					.add(aggregationOperationConstants.PROJECT_ITEMDROPS_FOR_SEGMENTED_WEIGHTED_QUANTITIES_WITH_ITEMID);
			operations.add(Aggregation.match(Criteria.where("itemId").is(itemId)));
			operations.add(aggregationOperationConstants.GROUP_BY_SECTION_AND_ITEMID_SUM_WEIGHTED_QUANTITY_WITH_ITEMID);
			operations.add(aggregationOperationConstants.PROJECT_SECTION_FOR_SEGMENTED_WEIGHTED_QUANTITIES_WITH_ITEMID);
		}
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
	 * @Title: aggregateSegmentedWeightedStageTimes 
	 * @Description: Use aggregation to get weighted upload times for each stage under each time point for each interval.
	 * @param criteria The filter used in the first 'match' stage.
	 * @param stageId Required.
	 * @param startTime The earliest upload time of this stage.
	 * @param interval
	 * @return List<Document> In each document, _id is section, and allTimes contain several embedded documents describing stage times.
	 */
	/** pipeline:
		[{
		    $match: {
		        isReliable: true,
		        isDeleted: false,
		        stageId: 'main_01-07'
		    }
		}, {
		    $project: {
		        _id: 0,
		        times: 1,
		        userID: 1,
		        timestamp: 1,
		        section: {
		            $trunc: {
		                $divide: [{
		                        $subtract: [
		                            '$timestamp',
		                            1558530453551
		                        ]
		                    },
		                    86400000
		                ]
		            }
		        }
		    }
		}, {
		    $group: {
		        _id: '$section',
		        itemDrops: {
		            $push: {
		                itemDrop: '$$ROOT'
		            }
		        }
		    }
		}, {
		    $project: {
		        _id: 0,
		        section: '$_id',
		        itemDrops: 1
		    }
		}, {
		    $unwind: {
		        path: "$itemDrops",
		        preserveNullAndEmptyArrays: false
		    }
		}, {
		    $project: {
		        times: "$itemDrops.itemDrop.times",
		        timestamp: "$itemDrops.itemDrop.timestamp",
		        userID: "$itemDrops.itemDrop.userID",
		        section: "$section"
		    }
		}, {
		    $group: {
		        _id: '$userID',
		        itemDrops: {
		            $push: {
		                section: '$$ROOT.section',
		                times: '$$ROOT.times',
		                timestamp: '$$ROOT.timestamp'
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
		            $arrayElemAt: [
		                '$user.weight',
		                0
		            ]
		        }
		    }
		}, {
		    $unwind: {
		        path: '$itemDrops',
		        preserveNullAndEmptyArrays: false
		    }
		}, {
		    $addFields: {
		        addTime: [
		            0,
		            1558989300000,
		            1560045456000
		        ]
		    }
		}, {
		    $unwind: {
		        path: '$addTime',
		        includeArrayIndex: 'point',
		        preserveNullAndEmptyArrays: true
		    }
		}, {
		    $project: {
		        section: '$itemDrops.section',
		        point: 1,
		        weight: 1,
		        times: {
		            $cond: {
		                if: {
		                    $gt: [
		                        '$itemDrops.timestamp',
		                        '$addTime'
		                    ]
		                },
		                then: 1,
		                else: 0
		            }
		        }
		    }
		}, {
		    $group: {
		        _id: {
		            section: '$section',
		            point: '$point'
		        },
		        times: {
		            $sum: {
		                $multiply: [
		                    '$times',
		                    '$weight'
		                ]
		            }
		        }
		    }
		}, {
		    $group: {
		        _id: '$_id.section',
		        allTimes: {
		            $push: {
		                timePoint: '$_id.point',
		                times: '$times'
		            }
		        }
		    }
		}]
	 */
	@Override
	public List<Document> aggregateSegmentedWeightedStageTimes(Criteria criteria, String stageId, long startTime,
			long interval) {
		List<AggregationOperation> operations = new LinkedList<>();
		operations.add(aggregationOperationConstants.MATCH_RELIEABLE_NOT_DELETED);
		operations.add(Aggregation.match(Criteria.where("stageId").is(stageId)));
		operations.add(Aggregation.project("times", "userID", "timestamp")
				.and(ArithmeticOperators.Trunc.truncValueOf(ArithmeticOperators.Divide
						.valueOf(ArithmeticOperators.Subtract.valueOf("timestamp").subtract(startTime))
						.divideBy(interval)))
				.as("section"));
		operations.add(aggregationOperationConstants.GROUP_BY_SECTION);
		operations.add(aggregationOperationConstants.UNWIND_ITEMDROPS);
		operations.add(aggregationOperationConstants.PROJECT_SECTION_FOR_SEGMENTED_WEIGHTED_TIMES);
		operations.add(aggregationOperationConstants.GROUP_BY_USERID_FOR_SEGMENTED_WEIGHTED_STAGE_TIMES);
		operations.add(aggregationOperationConstants.LOOKUP_USER);
		operations.add(aggregationOperationConstants.PROJECT_WEIGHT);
		operations.add(aggregationOperationConstants.UNWIND_ITEMDROPS);
		operations.add(aggregationOperationConstants.PROJECT_ADD_TIME_FOR_WEIGHTED_STAGE_TIMES);
		operations.add(aggregationOperationConstants.UNWIND_ADD_TIME);
		operations.add(aggregationOperationConstants.PROJECT_TIMESTAMP_GT_ADD_TIME_FOR_SEGMENTED_WEIGHTED_STAGE_TIMES);
		operations.add(aggregationOperationConstants.GROUP_BY_SECTION_AND_POINT_SUM_WEIGHTED_TIMES);
		operations.add(aggregationOperationConstants.GROUP_BY_SECTION_FOR_SEGMENTED_WEIGHTED_STAGE_TIMES);
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

	@Override
	public Long findMinTimestamp(Boolean isReliable, Boolean isDeleted, String stageId) {
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where("isReliable").is(isReliable),
				Criteria.where("isDeleted").is(isDeleted), Criteria.where("stageId").is(stageId)));
		query.with(Sort.by(Sort.Order.asc("timestamp")));
		query.limit(1);
		ItemDrop itemDrop = mongoTemplate.findOne(query, ItemDrop.class);
		return itemDrop == null ? null : itemDrop.getTimestamp();
	}

}
