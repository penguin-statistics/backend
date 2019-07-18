package io.penguinstats.dao;

import static com.mongodb.client.model.Accumulators.push;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.addFields;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.unwind;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Projections.computed;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.UnwindOptions;

import io.penguinstats.bean.ItemDrop;
import io.penguinstats.service.ItemDropService;

public class ItemDropDao extends BaseDao<ItemDrop> {

	private static Logger logger = LogManager.getLogger(ItemDropDao.class);

	public ItemDropDao() {
		super("item_drop_v2");
	}

	/**
	 * @Title: findAllReliableItemDrops
	 * @Description: Return a list of all reliable item drop records.
	 * @return List<ItemDrop>
	 */
	public List<ItemDrop> findAllReliableItemDrops() {
		List<ItemDrop> itemDrops = new ArrayList<>();
		MongoCursor<Document> iter = collection.find(eq("isReliable", true)).iterator();
		while (iter.hasNext()) {
			Document document = iter.next();
			itemDrops.add(new ItemDrop(document));
		}
		return itemDrops;
	}

	public List<ItemDrop> findAllReliableItemDropsByStageId(String stageId) {
		List<ItemDrop> itemDrops = new ArrayList<>();
		MongoCursor<Document> iter = collection.find(and(eq("isReliable", true), eq("stageId", stageId))).iterator();
		while (iter.hasNext()) {
			Document document = iter.next();
			itemDrops.add(new ItemDrop(document));
		}
		return itemDrops;
	}

	/**
	 * @Title: findAllDropsByUserId
	 * @Description: Return a list of all item drop records uploaded by the given userID.
	 * @param userID
	 * @return List<ItemDrop>
	 */
	public List<ItemDrop> findAllDropsByUserId(String userID) {
		List<ItemDrop> itemDrops = new ArrayList<>();
		MongoCursor<Document> iter = collection.find(eq("userID", userID)).iterator();
		while (iter.hasNext()) {
			Document document = iter.next();
			itemDrops.add(new ItemDrop(document));
		}
		return itemDrops;
	}

	/** 
	 * @Title: aggregateItemDropQuantities 
	 * @Description: Use aggregation to get all item drop quantities under each stage.
	 * @param filter The filter used in the first 'match' stage.
	 * @return Map<String,Map<String,Integer>> stageId -> itemId -> quantity
	 */
	/** pipeline:
		[{
		    $match: {
		        isReliable: true,
		    }
		}, {
		    $unwind: {
		        path: "$drops",
		        preserveNullAndEmptyArrays: false
		    }
		}, {
		    $group: {
		        _id: {
		            stageId: "$stageId",
		            itemId: "$drops.itemId"
		        },
		        quantity: {
		            $sum: "$drops.quantity"
		        }
		    }
		}, {
		    $project: {
		        stageId: "$_id.stageId",
		        itemId: "$_id.itemId",
		        quantity: 1,
		        _id: 0
		    }
		}]
	 */
	public Map<String, Map<String, Integer>> aggregateItemDropQuantities(Bson filter) {
		Long startTime = System.currentTimeMillis();
		if (filter == null)
			filter = new Document();
		List<Bson> pipeline =
				Arrays.asList(match(filter), unwind("$drops", new UnwindOptions().preserveNullAndEmptyArrays(false)),
						group(and(eq("stageId", "$stageId"), eq("itemId", "$drops.itemId")),
								sum("quantity", "$drops.quantity")),
						project(fields(computed("stageId", "$_id.stageId"), computed("itemId", "$_id.itemId"),
								include("quantity"), excludeId())));
		MongoCursor<Document> iter = this.collection.aggregate(pipeline).iterator();
		Map<String, Map<String, Integer>> map = new HashMap<>();
		while (iter.hasNext()) {
			Document doc = iter.next();
			String stageId = doc.getString("stageId");
			String itemId = doc.getString("itemId");
			Integer quantity = doc.getInteger("quantity");
			if (stageId != null && itemId != null && quantity != null) {
				Map<String, Integer> subMap = map.getOrDefault(stageId, new HashMap<>());
				subMap.put(itemId, quantity);
				map.put(stageId, subMap);
			}
		}
		logger.debug(
				"aggregateItemDropQuantities " + (System.currentTimeMillis() - startTime) + "ms " + filter.toString());
		return map;
	}

	/** 
	 * @Title: aggregateStageTimes 
	 * @Description: Use aggregation to get upload times for each stage under every time point.
	 * @param filter The filter used in the first 'match' stage.
	 * @return Map<String,List<Integer>> stageId -> times list
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
		    $match: {
		        $expr: {
		            $gt: ["$timestamp", "$addTime"]
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
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Map<String, List<Integer>> aggregateStageTimes(Bson filter) {
		Long startTime = System.currentTimeMillis();
		if (filter == null)
			filter = new Document();
		List<Bson> pipeline = Arrays.asList(match(filter),
				addFields(new Field("addTime", Arrays.asList(ItemDropService.ADD_TIME_POINTS))),
				unwind("$addTime", new UnwindOptions().includeArrayIndex("point").preserveNullAndEmptyArrays(true)),
				match(gt("$expr", Arrays.asList("$timestamp", "$addTime"))),
				group(and(eq("stageId", "$stageId"), eq("point", "$point")), sum("times", "$times")),
				group("$_id.stageId", push("allTimes", and(eq("timePoint", "$_id.point"), eq("times", "$times")))));
		MongoCursor<Document> iter = this.collection.aggregate(pipeline).iterator();
		Map<String, List<Integer>> map = new HashMap<>();
		while (iter.hasNext()) {
			Document doc = iter.next();
			String stageId = doc.getString("_id");
			List<Document> allTimesDocs = (ArrayList<Document>)doc.get("allTimes");
			int size = allTimesDocs.size();
			Integer[] allTimesArray = new Integer[size];
			for (int i = 0; i < size; i++) {
				Document subDoc = allTimesDocs.get(i);
				Integer timePoint = subDoc.getLong("timePoint").intValue();
				allTimesArray[timePoint] = subDoc.getInteger("times");
			}
			map.put(stageId, Arrays.asList(allTimesArray));
		}
		logger.debug("aggregateStageTimes " + (System.currentTimeMillis() - startTime) + "ms " + filter.toString());
		return map;
	}

	public void changeUserID(String oldID, String newID) {
		collection.updateMany(eq("userID", oldID), set("userID", newID));
	}

}
