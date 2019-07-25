package io.penguinstats.service;

import io.penguinstats.dao.ItemDropDao;
import io.penguinstats.model.DropMatrix;
import io.penguinstats.model.Item;
import io.penguinstats.model.ItemDrop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("itemDropService")
public class ItemDropServiceImpl implements ItemDropService {

	private static Logger logger = LogManager.getLogger(ItemDropServiceImpl.class);

	@Autowired
	private ItemService itemService;

	@Autowired
	private ItemDropDao itemDropDao;

	public void saveItemDrop(ItemDrop itemDrop) {
		itemDropDao.save(itemDrop);
	}

	public void deleteItemDrop(String userID, String itemDropId) throws Exception {
		ItemDrop itemDrop = itemDropDao.findById(itemDropId).orElse(null);
		if (itemDrop == null || !itemDrop.getUserID().equals(userID)) {
			throw new Exception("ItemDrop[" + itemDropId + "] not found for user with ID[" + userID + "]");
		}

		itemDrop.setIsDeleted(true);
		itemDropDao.save(itemDrop);
	}

	public List<ItemDrop> getAllItemDrops() {
		return itemDropDao.findAll();
	}

	public List<ItemDrop> getAllReliableItemDrops() {
		return itemDropDao.findByIsReliable(true);
	}

	public List<ItemDrop> getItemDropsByUserID(String userID) {
		return itemDropDao.findByUserID(userID);
	}

	public Page<ItemDrop> getVisibleItemDropsByUserID(String userID, Pageable pageable) {
		return itemDropDao.findByIsDeletedAndUserID(false, userID, pageable);
	}

	/** 
	 * @Title: getStageTimesMap 
	 * @Description: Get upload times for each stage under every time point.
	 * @param filter The filter used in the first 'match' stage.
	 * @return Map<String,List<Integer>> stageId -> times list
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<Integer>> getStageTimesMap(Criteria filter) {
		Long startTime = System.currentTimeMillis();
		List<Document> resultList = itemDropDao.aggregateStageTimes(filter);
		Iterator<Document> iter = resultList.iterator();
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
		logger.debug("aggregateStageTimes " + (System.currentTimeMillis() - startTime) + "ms");
		return map;
	}

	/** 
	 * @Title: getQuantitiesMap 
	 * @Description: Get all item drop quantities under each stage.
	 * @param filter The filter used in the first 'match' stage.
	 * @return Map<String,Map<String,Integer>> stageId -> itemId -> quantity
	 */
	public Map<String, Map<String, Integer>> getQuantitiesMap(Criteria filter) {
		Long startTime = System.currentTimeMillis();
		List<Document> resultList = itemDropDao.aggregateItemDropQuantities(filter);
		Iterator<Document> iter = resultList.iterator();
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
		logger.debug("aggregateItemDropQuantities " + (System.currentTimeMillis() - startTime) + "ms");
		return map;
	}

	/** 
	 * @Title: generateDropMatrixList 
	 * @Description: Generate a list of sparse matrix elements from drop records filtered by given filter using aggregation pipelines.
	 * @param filter
	 * @return List<DropMatrix>
	 */
	public List<DropMatrix> generateDropMatrixList(Criteria filter) {
		Long startTime = System.currentTimeMillis();
		List<DropMatrix> dropMatrixList = new ArrayList<>();
		try {
			Map<String, Map<String, Integer>> quantitiesMap = getQuantitiesMap(filter);
			Map<String, List<Integer>> stageTimesMap = getStageTimesMap(filter);
			Map<String, Item> itemMap = itemService.getItemMap();
			for (String stageId : quantitiesMap.keySet()) {
				List<Integer> allTimes = stageTimesMap.get(stageId);
				if (allTimes == null) {
					logger.error("cannot find allTimes for " + stageId);
					continue;
				}
				Map<String, Integer> subMap = quantitiesMap.get(stageId);
				for (String itemId : subMap.keySet()) {
					Integer quantity = subMap.get(itemId);
					Item item = itemMap.get(itemId);
					if (item == null) {
						logger.error("cannot find item " + itemId);
						continue;
					}
					Integer addTimePoint = item.getAddTimePoint();
					if (addTimePoint == null)
						addTimePoint = 0;
					if (addTimePoint >= allTimes.size()) {
						logger.error("addTimePoint for " + itemId + " is too large");
						continue;
					}
					Integer times = allTimes.get(addTimePoint);
					dropMatrixList.add(new DropMatrix(stageId, itemId, quantity, times));
				}
			}
			logger.debug(
					"generateDropMatrixList " + (System.currentTimeMillis() - startTime) + "ms " + filter.toString());
		} catch (Exception e) {
			logger.error("Error in generateDropMatrixList", e);
		}
		return dropMatrixList;
	}

	/** 
	 * @Title: generateDropMatrixMap
	 * @Description: Generate a map of sparse matrix elements from drop records filtered by given filter using aggregation pipelines.
	 * @param filter
	 * @return Map<String,Map<String,DropMatrix>> stageId -> itemId -> dropMatrix
	 */
	public Map<String, Map<String, DropMatrix>> generateDropMatrixMap(Criteria filter) {
		Map<String, Map<String, DropMatrix>> map = new HashMap<>();
		List<DropMatrix> list = generateDropMatrixList(filter);
		for (DropMatrix dm : list) {
			Map<String, DropMatrix> subMap = map.getOrDefault(dm.getStageId(), new HashMap<>());
			subMap.put(dm.getItemId(), dm);
			map.put(dm.getStageId(), subMap);
		}
		return map;
	}

}
