package io.penguinstats.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import io.penguinstats.constant.Constant;
import io.penguinstats.dao.ItemDropDao;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.Item;
import io.penguinstats.model.ItemDrop;
import io.penguinstats.model.QueryConditions;
import io.penguinstats.model.Stage;
import io.penguinstats.model.TimeRange;
import io.penguinstats.util.HashUtil;
import io.penguinstats.util.LastUpdateTimeUtil;

@Service("itemDropService")
public class ItemDropServiceImpl implements ItemDropService {

	private static Logger logger = LogManager.getLogger(ItemDropServiceImpl.class);

	@Autowired
	private ItemService itemService;

	@Autowired
	private StageService stageService;

	@Autowired
	private ItemDropDao itemDropDao;

	@Autowired
	private DropInfoService dropInfoService;

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
	public void recallItemDrop(String userID, String itemDropHashId) throws Exception {
		Pageable pageable = PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "timestamp"));
		List<ItemDrop> itemDropList = getVisibleItemDropsByUserID(userID, pageable).getContent();
		if (itemDropList.size() == 0) {
			throw new Exception("Visible ItemDrop not found for user with ID[" + userID + "]");
		}

		ItemDrop lastItemDrop = itemDropList.get(0);
		String lastItemDropHashId = HashUtil.getHash(lastItemDrop.getId().toString());
		if (!lastItemDropHashId.equals(itemDropHashId)) {
			throw new Exception("ItemDropHashId doesn't match!");
		}

		lastItemDrop.setIsDeleted(true);
		itemDropDao.save(lastItemDrop);
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
						// Sometimes item may be null because it has been removed from dropSet 
						logger.warn("cannot find item " + itemId);
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
						dropMatrixList.add(new DropMatrixElement(stageId, itemId, quantity, times, null, null));
				}
			}
			logger.debug("generateDropMatrixElements " + (System.currentTimeMillis() - startTime) + "ms");
		} catch (Exception e) {
			logger.error("Error in generateDropMatrixElements", e);
		}
		LastUpdateTimeUtil
				.setCurrentTimestamp(isWeighted ? "weightedDropMatrixElements" : "notWeightedDropMatrixElements");
		return dropMatrixList;
	}

	/** 
	 * @Title: generateDropMatrixElements 
	 * @Description: Generate segmented drop results for the given stage and interval.
	 * @param filter
	 * @param interval
	 * @param startTime
	 * @param stageId Required.
	 * @param itemId Optional. If itemId is provided, the result map will only contain one key, otherwise it will contain all itemIds under the given stage (must have at least one drop record). 
	 * @return Map<String,List<DropMatrixElement>> itemId -> result list (index is section#, if no drop in one section, the element will be null)
	 */
	@Override
	public Map<String, List<DropMatrixElement>> generateDropMatrixElements(Criteria filter, long interval,
			Long startTime, String stageId, String itemId) {
		Map<String, List<DropMatrixElement>> result =
				generateSegmentedDropMatrixElementsHelper(filter, interval, startTime, stageId, itemId);
		LastUpdateTimeUtil
				.setCurrentTimestamp("segmentedDropMatrixElements_" + stageId + (itemId == null ? "" : "_" + itemId));
		return result;
	}

	/** 
	 * @Title: generateDropMatrixElements 
	 * @Description: Generate segmented drop results for all stages under given interval.
	 * @param filter
	 * @param interval
	 * @return Map<String,Map<String,List<DropMatrixElement>>> stageId -> itemId -> result list
	 */
	@Override
	public Map<String, Map<String, List<DropMatrixElement>>> generateDropMatrixElements(Criteria filter,
			long interval) {
		Map<String, Map<String, List<DropMatrixElement>>> result = new HashMap<>();
		//		List<Stage> stages = stageService.getAllStages();
		//		for (Stage stage : stages) {
		//			String stageId = stage.getStageId();
		//			Long startTime = getMinTimestamp(stageId);
		//			try {
		//				Map<String, List<DropMatrixElement>> subMap =
		//						generateSegmentedDropMatrixElementsHelper(filter, interval, startTime, stageId, null);
		//				result.put(stageId, subMap);
		//			} catch (Exception e) {
		//				logger.error("Error in generateDropMatrixElements", e);
		//			}
		//		}
		LastUpdateTimeUtil.setCurrentTimestamp("segmentedDropMatrixElements");
		return result;
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
	 * @Title: updateDropMatrixElements 
	 * @Description: Update matrix by generating a new one.
	 * @param filter
	 * @param isWeighted
	 * @return List<DropMatrixElement>
	 */
	@Override
	public List<DropMatrixElement> updateDropMatrixElements(Criteria filter, boolean isWeighted) {
		return generateDropMatrixElements(filter, isWeighted);
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

	/** 
	 * @Title: getMinTimestamp 
	 * @Description: Get the earliest upload time of one stage
	 * @param stageId
	 * @return Long
	 */
	@Override
	public Long getMinTimestamp(String stageId) {
		return itemDropDao.findMinTimestamp(true, false, stageId);
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<DropMatrixElement>> generateSegmentedDropMatrixElementsHelper(Criteria filter,
			long interval, Long startTime, String stageId, String itemId) {
		Map<String, List<DropMatrixElement>> segmentedDropMap = new HashMap<>();
		if (startTime == null)
			return segmentedDropMap;
		Map<String, Item> itemMap = itemService.getItemMap();
		Map<String, Stage> stageMap = stageService.getStageMap();
		if (!stageMap.containsKey(stageId)) {
			logger.error("cannot find stage " + stageId);
			return segmentedDropMap;
		}
		int sectionNum = new Long((System.currentTimeMillis() - startTime) / interval).intValue() + 1;
		if (sectionNum > Constant.MAX_SECTION_NUM) {
			logger.error("Section num is too large. MAX is " + Constant.MAX_SECTION_NUM + ", current is " + sectionNum);
			return segmentedDropMap;
		}

		List<Document> quantityDocs =
				itemDropDao.aggregateSegmentedWeightedItemDropQuantities(filter, stageId, startTime, interval, itemId);
		List<Document> timesDocs =
				itemDropDao.aggregateSegmentedWeightedStageTimes(filter, stageId, startTime, interval);

		List<List<Double>> stageTimesList = new ArrayList<>(sectionNum);
		for (int i = 0; i < sectionNum; i++)
			stageTimesList.add(new ArrayList<>());
		for (Document doc : timesDocs) {
			int section = doc.getDouble("_id").intValue();
			List<Document> allTimesDocs = (ArrayList<Document>)doc.get("allTimes");
			int size = allTimesDocs.size();
			Double[] allTimesArray = new Double[size];
			for (int i = 0; i < size; i++) {
				Document subDoc = allTimesDocs.get(i);
				Integer timePoint = subDoc.getLong("timePoint").intValue();
				allTimesArray[timePoint] = subDoc.getDouble("times");
			}
			stageTimesList.set(section, Arrays.asList(allTimesArray));
		}

		List<Map<String, Double>> itemQuantitiesList = new ArrayList<>();
		for (int i = 0; i < sectionNum; i++)
			itemQuantitiesList.add(new HashMap<>());
		for (Document doc : quantityDocs) {
			int section = doc.getDouble("section").intValue();
			String currentItemId = itemId == null ? doc.getString("itemId") : itemId;
			Double quantity = doc.getDouble("quantity");
			Map<String, Double> quantityMap = itemQuantitiesList.get(section);
			quantityMap.put(currentItemId, quantity);
		}

		Stage stage = stageMap.get(stageId);
		Set<String> dropSet = stage.getDropsSet();
		for (int i = 0; i < sectionNum; i++) {
			List<Double> allTimes = stageTimesList.get(i);
			if (allTimes.isEmpty())
				continue;
			Map<String, Double> quantityMap = itemQuantitiesList.get(i);
			for (String currentItemId : dropSet) {
				Integer quantity = new Long(Math.round(quantityMap.getOrDefault(currentItemId, 0D))).intValue();
				Item item = itemMap.get(currentItemId);
				if (item == null) {
					logger.warn("cannot find item " + currentItemId);
					continue;
				}
				Integer addTimePoint = item.getAddTimePoint();
				if (addTimePoint == null)
					addTimePoint = 0;
				if (addTimePoint >= allTimes.size()) {
					logger.error("addTimePoint for " + currentItemId + " is too large");
					continue;
				}
				Integer times = new Long(Math.round(allTimes.get(addTimePoint))).intValue();
				if (!times.equals(0)) {
					List<DropMatrixElement> elements = segmentedDropMap.getOrDefault(currentItemId, new ArrayList<>());
					if (elements.isEmpty()) {
						for (int j = 0; j < sectionNum; j++)
							elements.add(null);
					}
					elements.set(i, new DropMatrixElement(stageId, currentItemId, quantity, times, null, null));
					segmentedDropMap.put(currentItemId, elements);
				}
			}
		}
		return segmentedDropMap;
	}

	@Override
	public List<DropMatrixElement> generateGlobalDropMatrixElements(Server server, String userID) {
		Map<String, List<TimeRange>> timeRangeMap =
				dropInfoService.getLatestMaxAccumulatableTimeRangesMapByServer(server);
		return generateDropMatrixElementsFromTimeRangeMapByStageId(timeRangeMap, server, userID);
	}

	private List<DropMatrixElement> generateDropMatrixElementsFromTimeRangeMapByStageId(
			Map<String, List<TimeRange>> timeRangeMap, Server server, String userID) {
		Map<String, List<String>> stagesMapByRange = transferTimeRangeMapByStageIdToStagesMapByRange(timeRangeMap);

		Map<String, Map<String, List<Document>>> docsMapByStageIdAndTimeRange = new HashMap<>();
		stagesMapByRange.forEach((key, stages) -> {
			QueryConditions conditions = new QueryConditions();
			// TODO: uncomment this
			//			if (server != null)
			// conditions.addServer(server);
			if (userID != null)
				conditions.addUserID(userID);
			Long[] timestamps = timeRangeStrToLong(key);
			Long start = timestamps[0];
			Long end = timestamps[1];
			stages.forEach(stageId -> conditions.addStage(stageId, start, end));

			List<Document> docs = itemDropDao.aggregateItemDropQuantities(conditions);
			docs.forEach(doc -> {
				String stageId = doc.getString("stageId");
				Map<String, List<Document>> docsMapByStart =
						docsMapByStageIdAndTimeRange.getOrDefault(stageId, new HashMap<>());
				List<Document> subDocs = docsMapByStart.getOrDefault(key, new ArrayList<>());
				subDocs.add(doc);
				docsMapByStart.put(key, subDocs);
				docsMapByStageIdAndTimeRange.put(stageId, docsMapByStart);
			});
		});

		Map<String, Map<String, List<DropMatrixElement>>> mapByStageIdAndItemId = new HashMap<>();
		docsMapByStageIdAndTimeRange.forEach((stageId, docsMapByStart) -> {
			docsMapByStart.forEach((key, docs) -> {
				Long[] timestamps = timeRangeStrToLong(key);
				Long start = timestamps[0];
				Long end = timestamps[1];
				Integer timesForStage = docs.get(0).getInteger("times");
				Set<String> dropSet = dropInfoService.getDropSet(server, stageId, start);
				docs.forEach(doc -> {
					String itemId = doc.getString("itemId");
					Integer quantity = doc.getInteger("quantity");
					Integer times = doc.getInteger("times");
					DropMatrixElement element = new DropMatrixElement(stageId, itemId, quantity, times, start, end);

					Map<String, List<DropMatrixElement>> mapByItemId =
							mapByStageIdAndItemId.getOrDefault(stageId, new HashMap<>());
					List<DropMatrixElement> elements = mapByItemId.getOrDefault(itemId, new ArrayList<>());
					elements.add(element);
					mapByItemId.put(itemId, elements);
					mapByStageIdAndItemId.put(stageId, mapByItemId);

					if (!dropSet.contains(itemId))
						logger.error("Item " + itemId + " is invalid in stage " + stageId);
					else
						dropSet.remove(itemId);
				});

				if (!dropSet.isEmpty()) {
					dropSet.forEach(itemId -> {
						DropMatrixElement element =
								new DropMatrixElement(stageId, itemId, 0, timesForStage, start, end);
						Map<String, List<DropMatrixElement>> mapByItemId =
								mapByStageIdAndItemId.getOrDefault(stageId, new HashMap<>());
						List<DropMatrixElement> elements = mapByItemId.getOrDefault(itemId, new ArrayList<>());
						elements.add(element);
						mapByItemId.put(itemId, elements);
						mapByStageIdAndItemId.put(stageId, mapByItemId);
					});
				}
			});
		});

		List<DropMatrixElement> result = new ArrayList<>();
		mapByStageIdAndItemId.forEach((stageId, mapByItemId) -> {
			mapByItemId.forEach((itemId, elements) -> {
				DropMatrixElement newElement = combineElements(elements);
				result.add(newElement);
			});
		});
		return result;
	}

	private Map<String, List<String>>
			transferTimeRangeMapByStageIdToStagesMapByRange(Map<String, List<TimeRange>> timeRangeMap) {
		Map<String, List<String>> stagesMapByRange = new HashMap<>();
		timeRangeMap.forEach((stageId, ranges) -> {
			ranges.forEach(range -> {
				String key = timeRangeLongToStr(range.getStart(), range.getEnd());
				List<String> stages = stagesMapByRange.getOrDefault(key, new ArrayList<>());
				stages.add(stageId);
				stagesMapByRange.put(key, stages);
			});
		});
		return stagesMapByRange;
	}

	private Long[] timeRangeStrToLong(String str) {
		String[] strs = str.split("_");
		Long start = "null".equals(strs[0]) ? null : Long.parseLong(strs[0]);
		Long end = "null".equals(strs[1]) ? null : Long.parseLong(strs[1]);
		return new Long[] {start, end};
	}

	private String timeRangeLongToStr(Long start, Long end) {
		return start + "_" + end;
	}

	private DropMatrixElement combineElements(List<DropMatrixElement> elements) {
		if (elements.isEmpty())
			return null;
		DropMatrixElement firstElement = elements.get(0);
		String stageId = firstElement.getStageId();
		String itemId = firstElement.getItemId();
		Integer quantity = 0;
		Integer times = 0;
		Long start = firstElement.getStart();
		Long end = firstElement.getEnd();
		for (int i = 0, l = elements.size(); i < l; i++) {
			DropMatrixElement element = elements.get(i);
			quantity += element.getQuantity();
			times += element.getTimes();
			if (element.getStart().compareTo(start) < 0)
				start = element.getStart();
			if (end != null && (element.getEnd() == null || element.getEnd().compareTo(end) > 0))
				end = element.getEnd();
		}
		return new DropMatrixElement(stageId, itemId, quantity, times, start, end);
	}

	@Override
	public Map<String, Map<String, List<DropMatrixElement>>> generateSegmentedGlobalDropMatrixElementMap(Server server,
			Integer interval, Long start, Long end) {
		if (start == null || end == null || start.compareTo(end) >= 0)
			return new HashMap<>();
		Long intervalMillis = TimeUnit.DAYS.toMillis(interval);
		int sectionNum =
				new Double(Math.ceil(new Double((end - start) * 1.0 / intervalMillis).doubleValue())).intValue();
		if (sectionNum > Constant.MAX_SECTION_NUM) {
			logger.error("exceed max section num, now is " + sectionNum);
			return new HashMap<>();
		}

		QueryConditions conditions = new QueryConditions();
		// TODO: uncomment this
		//		conditions.addServer(server);
		conditions.addStage(null, start, end);
		conditions.setInterval(interval);
		List<Document> docs = itemDropDao.aggregateItemDropQuantities(conditions);

		Map<String, Map<String, List<DropMatrixElement>>> map = new HashMap<>();
		Map<String, Map<Integer, Integer>> timesMap = new HashMap<>();
		docs.forEach(doc -> {
			String stageId = doc.getString("stageId");
			String itemId = doc.getString("itemId");
			Integer quantity = doc.getInteger("quantity");
			Integer times = doc.getInteger("times");
			Integer section = doc.getDouble("section").intValue();

			DropMatrixElement element =
					new DropMatrixElement(stageId, itemId, quantity, times, new Long(section), null);

			Map<String, List<DropMatrixElement>> subMap = map.getOrDefault(stageId, new HashMap<>());
			List<DropMatrixElement> elements = subMap.getOrDefault(itemId, new ArrayList<>());
			elements.add(element);
			subMap.put(itemId, elements);
			map.put(stageId, subMap);

			Map<Integer, Integer> timesMapSubMap = timesMap.getOrDefault(stageId, new HashMap<>());
			timesMapSubMap.put(section, times);
			timesMap.put(stageId, timesMapSubMap);
		});

		map.forEach((stageId, subMap) -> {
			Map<Integer, Integer> timesSubMap = timesMap.get(stageId);
			subMap.forEach((itemId, elements) -> {
				Set<Integer> sectionSet = new HashSet<>();
				for (int i = 0; i < sectionNum; i++)
					sectionSet.add(i);
				elements.forEach(el -> sectionSet.remove(el.getStart().intValue()));
				sectionSet.forEach(section -> {
					Integer times =
							timesSubMap == null || !timesSubMap.containsKey(section) ? 0 : timesSubMap.get(section);
					DropMatrixElement newElement =
							new DropMatrixElement(stageId, itemId, 0, times, new Long(section), null);
					elements.add(newElement);
				});
				elements.sort((e1, e2) -> e1.getStart().compareTo(e2.getStart()));
				elements.forEach(el -> {
					el.setStart(el.getStart() * intervalMillis + start);
					el.setEnd(el.getStart() + intervalMillis);
				});
			});
		});
		return map;
	}

}
