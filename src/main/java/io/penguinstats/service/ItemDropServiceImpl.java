package io.penguinstats.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.ItemDropDao;
import io.penguinstats.model.DropMatrix;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.Item;
import io.penguinstats.model.ItemDrop;
import io.penguinstats.model.Stage;

@Service("itemDropService")
public class ItemDropServiceImpl implements ItemDropService {

	private static Logger logger = LogManager.getLogger(ItemDropServiceImpl.class);

	@Autowired
	private ItemService itemService;

	@Autowired
	private StageService stageService;

	@Autowired
	private ItemDropDao itemDropDao;

	@Override
	public void saveItemDrop(ItemDrop itemDrop) {
		itemDropDao.save(itemDrop);
	}

	@Override
	public void batchSaveItemDrops(Collection<ItemDrop> itemDrops) {
		itemDropDao.saveAll(itemDrops);
	}

	@Override
	public void deleteItemDrop(String userID, String itemDropId) throws Exception {
		ItemDrop itemDrop = itemDropDao.findById(itemDropId).orElse(null);
		if (itemDrop == null || !itemDrop.getUserID().equals(userID)) {
			throw new Exception("ItemDrop[" + itemDropId + "] not found for user with ID[" + userID + "]");
		}

		itemDrop.setIsDeleted(true);
		itemDropDao.save(itemDrop);
	}

	@Override
	public List<ItemDrop> getAllItemDrops() {
		return itemDropDao.findAll();
	}

	@Override
	public List<ItemDrop> getAllReliableItemDrops() {
		return itemDropDao.findByIsReliable(true);
	}

	@Override
	public Page<ItemDrop> getVisibleItemDropsByUserID(String userID, Pageable pageable) {
		return itemDropDao.findByIsDeletedAndUserID(false, userID, pageable);
	}

	@Override
	public List<ItemDrop> getItemDropsByUserID(String userID) {
		return itemDropDao.findByUserID(userID);
	}

	/** 
	 * @Title: getStageTimesMap 
	 * @Description: Get upload times for each stage under every time point.
	 * @param filter The filter used in the first 'match' stage.
	 * @param isWeighted
	 * @return Map<String,List<Double>> stageId -> times list
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<Double>> getStageTimesMap(Criteria filter, boolean isWeighted) {
		Long startTime = System.currentTimeMillis();
		List<Document> resultList =
				isWeighted ? itemDropDao.aggregateWeightedStageTimes(filter) : itemDropDao.aggregateStageTimes(filter);
		Iterator<Document> iter = resultList.iterator();
		Map<String, List<Double>> map = new HashMap<>();
		while (iter.hasNext()) {
			Document doc = iter.next();
			String stageId = doc.getString("_id");
			List<Document> allTimesDocs = (ArrayList<Document>)doc.get("allTimes");
			int size = allTimesDocs.size();
			Double[] allTimesArray = new Double[size];
			for (int i = 0; i < size; i++) {
				Document subDoc = allTimesDocs.get(i);
				Integer timePoint = subDoc.getLong("timePoint").intValue();
				allTimesArray[timePoint] =
						isWeighted ? subDoc.getDouble("times") : new Double(subDoc.getInteger("times"));
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
	 * @param isWeighted
	 * @return Map<String,Map<String,Double>> stageId -> itemId -> quantity
	 */
	@Override
	public Map<String, Map<String, Double>> getQuantitiesMap(Criteria filter, boolean isWeighted) {
		Long startTime = System.currentTimeMillis();
		List<Document> resultList = isWeighted ? itemDropDao.aggregateWeightedItemDropQuantities(filter)
				: itemDropDao.aggregateItemDropQuantities(filter);
		Iterator<Document> iter = resultList.iterator();
		Map<String, Map<String, Double>> map = new HashMap<>();
		while (iter.hasNext()) {
			Document doc = iter.next();
			String stageId = doc.getString("stageId");
			String itemId = doc.getString("itemId");
			Double quantity = isWeighted ? doc.getDouble("quantity") : new Double(doc.getInteger("quantity"));
			if (stageId != null && itemId != null && quantity != null) {
				Map<String, Double> subMap = map.getOrDefault(stageId, new HashMap<>());
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
	//TODO: This method will be deprecated after a periodic matrix generation method is implemented. Will use DropMatrixElement instead.
	@Override
	public List<DropMatrix> generateDropMatrixList(Criteria filter) {
		Long startTime = System.currentTimeMillis();
		List<DropMatrix> dropMatrixList = new ArrayList<>();
		try {
			Map<String, Map<String, Double>> quantitiesMap = getQuantitiesMap(filter, false);
			Map<String, List<Double>> stageTimesMap = getStageTimesMap(filter, false);
			Map<String, Item> itemMap = itemService.getItemMap();
			for (String stageId : quantitiesMap.keySet()) {
				List<Double> allTimes = stageTimesMap.get(stageId);
				if (allTimes == null) {
					logger.error("cannot find allTimes for " + stageId);
					continue;
				}
				Map<String, Double> subMap = quantitiesMap.get(stageId);
				for (String itemId : subMap.keySet()) {
					Double quantity = subMap.get(itemId);
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
					Double times = allTimes.get(addTimePoint);
					dropMatrixList.add(new DropMatrix(stageId, itemId, quantity.intValue(), times.intValue()));
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
	 * @Title: generateMatrixElements 
	 * @Description: Generate a list of sparse matrix elements from drop records filtered by given filter using aggregation pipelines.
	 * @param filter
	 * @return List<DropMatrixElement>
	 */
	@Override
	public List<DropMatrixElement> generateDropMatrixElements(Criteria filter, boolean isWeighted) {
		Long startTime = System.currentTimeMillis();
		List<DropMatrixElement> dropMatrixList = new ArrayList<>();
		try {
			Map<String, Map<String, Double>> quantitiesMap = getQuantitiesMap(filter, isWeighted);
			Map<String, List<Double>> stageTimesMap = getStageTimesMap(filter, isWeighted);
			Map<String, Item> itemMap = itemService.getItemMap();
			Map<String, Stage> stageMap = stageService.getStageMap();
			for (String stageId : quantitiesMap.keySet()) {
				Stage stage = stageMap.get(stageId);
				if (stage == null) {
					logger.error("cannot find stage " + stageId);
					continue;
				}
				List<Double> allTimes = stageTimesMap.get(stageId);
				if (allTimes == null) {
					logger.error("cannot find allTimes for " + stageId);
					continue;
				}
				Map<String, Double> subMap = quantitiesMap.get(stageId);
				Set<String> dropsSet = stage.getDropsSet();
				for (String itemId : dropsSet) {
					Integer quantity = new Long(Math.round(subMap.getOrDefault(itemId, 0D))).intValue();
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
					Integer times = new Long(Math.round(allTimes.get(addTimePoint))).intValue();
					if (!times.equals(0))
						dropMatrixList.add(new DropMatrixElement(stageId, itemId, quantity, times));
				}
			}
			logger.debug("generateDropMatrixElements " + (System.currentTimeMillis() - startTime) + "ms "
					+ filter.toString());
		} catch (Exception e) {
			logger.error("Error in generateDropMatrixElements", e);
		}
		return dropMatrixList;
	}

	/** 
	 * @Title: generateDropMatrixMap
	 * @Description: Generate a map of sparse matrix elements from drop records filtered by given filter using aggregation pipelines.
	 * @param filter
	 * @return Map<String,Map<String,DropMatrix>> stageId -> itemId -> dropMatrix
	 */
	//TODO: This method will be deprecated after a periodic matrix generation method is implemented. Will use WeightedMatrixElement instead.
	@Override
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

	/** 
	 * @Title: generateDropMatrixMap
	 * @Description: Generate a map of sparse matrix elements from drop records filtered by given filter using aggregation pipelines.
	 * @param filter
	 * @return Map<String,Map<String,DropMatrixElement>> stageId -> itemId -> dropMatrix
	 */
	@Override
	public Map<String, Map<String, DropMatrixElement>> generateDropMatrixMap(Criteria filter, boolean isWeighted) {
		Map<String, Map<String, DropMatrixElement>> map = new HashMap<>();
		List<DropMatrixElement> list = generateDropMatrixElements(filter, isWeighted);
		for (DropMatrixElement dm : list) {
			Map<String, DropMatrixElement> subMap = map.getOrDefault(dm.getStageId(), new HashMap<>());
			subMap.put(dm.getItemId(), dm);
			map.put(dm.getStageId(), subMap);
		}
		return map;
	}

	/** 
	 * @Title: generateUploadCountMap
	 * @Description: Generate a map of user's upload count under given criteria
	 * @param criteria
	 * @return Map<String, Integer> userID -> count
	 */
	@Override
	public Map<String, Integer> generateUploadCountMap(Criteria criteria) {
		Map<String, Integer> map = new HashMap<>();
		List<Document> docs = itemDropDao.aggregateUploadCount(criteria);
		for (Document doc : docs) {
			String userID = doc.getString("_id");
			if (userID != null)
				map.put(userID, doc.getInteger("count"));
		}
		return map;
	}

}
