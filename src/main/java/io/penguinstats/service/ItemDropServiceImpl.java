package io.penguinstats.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.constant.Constant.SystemPropertyKey;
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

	@Autowired
	private TimeRangeService timeRangeService;

	@Autowired
	private SystemPropertyService systemPropertyService;

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
	public Page<ItemDrop> getAllItemDrops(Pageable pageable) {
		return itemDropDao.findAll(pageable);
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

	@Override
	public List<DropMatrixElement> generateGlobalDropMatrixElements(Server server, String userID) {
		Long startTime = System.currentTimeMillis();

		Map<String, List<Pair<String, List<TimeRange>>>> latestMaxAccumulatableTimeRangesMap =
				timeRangeService.getLatestMaxAccumulatableTimeRangesMapByServer(server);

		Map<String, List<Pair<TimeRange, List<String>>>> convertedMap = new HashMap<>();
		latestMaxAccumulatableTimeRangesMap.forEach((stageId, pairs) -> {
			List<Pair<TimeRange, List<String>>> subList = new ArrayList<>();
			Map<TimeRange, List<String>> subMap = new HashMap<>();
			pairs.forEach(pair -> {
				String itemId = pair.getValue0();
				List<TimeRange> ranges = pair.getValue1();
				ranges.forEach(range -> {
					List<String> itemIds = subMap.getOrDefault(range, new ArrayList<>());
					itemIds.add(itemId);
					subMap.put(range, itemIds);
				});
			});
			subMap.forEach((range, itemIds) -> subList.add(Pair.with(range, itemIds)));
			convertedMap.put(stageId, subList);
		});

		Integer maxSize = null;
		for (String stageId : convertedMap.keySet()) {
			List<Pair<TimeRange, List<String>>> pairs = convertedMap.get(stageId);
			if (maxSize == null || maxSize < pairs.size())
				maxSize = pairs.size();
		}

		List<String> userIDs = userID != null ? Arrays.asList(userID) : new ArrayList<>();
		Map<String, Map<String, List<DropMatrixElement>>> allElementsMap = new HashMap<>();

		for (int i = 0; i < maxSize; i++) {
			Map<String, List<TimeRange>> timeRangeMap = new HashMap<>();
			for (String stageId : convertedMap.keySet()) {
				List<Pair<TimeRange, List<String>>> pairs = convertedMap.get(stageId);
				if (i >= pairs.size())
					continue;
				Pair<TimeRange, List<String>> pair = pairs.get(i);
				TimeRange range = pair.getValue0();
				timeRangeMap.put(stageId, Arrays.asList(range));
			}
			List<DropMatrixElement> elements = generateDropMatrixElementsFromTimeRangeMapByStageId(server, timeRangeMap,
					new ArrayList<>(), userIDs);

			for (String stageId : convertedMap.keySet()) {
				Map<String, List<DropMatrixElement>> subMap = allElementsMap.getOrDefault(stageId, new HashMap<>());
				List<Pair<TimeRange, List<String>>> pairs = convertedMap.get(stageId);
				if (i >= pairs.size())
					continue;
				Pair<TimeRange, List<String>> pair = pairs.get(i);
				Set<String> itemIdSet = new HashSet<>(pair.getValue1());
				List<DropMatrixElement> filteredElements = elements.stream()
						.filter(el -> el.getStageId().equals(stageId) && itemIdSet.contains(el.getItemId()))
						.collect(toList());
				filteredElements.forEach(el -> {
					String itemId = el.getItemId();
					List<DropMatrixElement> subList = subMap.getOrDefault(itemId, new ArrayList<>());
					subList.add(el);
					subMap.put(itemId, subList);
					itemIdSet.remove(itemId);
				});
				allElementsMap.put(stageId, subMap);
			}
		}

		List<DropMatrixElement> result = allElementsMap.values().stream()
				.flatMap(m -> m.values().stream().map(els -> combineElements(els))).collect(toList());

		if (userID == null)
			LastUpdateTimeUtil.setCurrentTimestamp(LastUpdateMapKeyName.MATRIX_RESULT + "_" + server);
		logger.info("generateGlobalDropMatrixElements done in {} ms", System.currentTimeMillis() - startTime);

		return result;
	}

	@Override
	public List<DropMatrixElement> refreshGlobalDropMatrixElements(Server server) {
		return generateGlobalDropMatrixElements(server, null);
	}

	private List<DropMatrixElement> generateDropMatrixElementsFromTimeRangeMapByStageId(Server server,
			Map<String, List<TimeRange>> timeRangeMap, List<String> itemIds, List<String> userIDs) {
		Integer maxSize = null;
		for (String stageId : timeRangeMap.keySet()) {
			List<TimeRange> ranges = timeRangeMap.get(stageId);
			if (maxSize == null || maxSize < ranges.size())
				maxSize = ranges.size();
		}

		Map<String, Map<String, List<DropMatrixElement>>> mapByStageIdAndItemId = new HashMap<>();
		for (int i = 0; i < maxSize; i++) {
			QueryConditions conditions = new QueryConditions();
			if (server != null)
				conditions.addServer(server);
			if (Optional.ofNullable(userIDs).map(list -> !list.isEmpty()).orElse(false)) {
				userIDs.forEach(userID -> conditions.addUserID(userID));
			}
			if (Optional.ofNullable(itemIds).map(list -> !list.isEmpty()).orElse(false)) {
				itemIds.forEach(itemId -> conditions.addItemId(itemId));
			}

			Map<String, TimeRange> currentRangesByStageId = new HashMap<>();
			for (String stageId : timeRangeMap.keySet()) {
				List<TimeRange> ranges = timeRangeMap.get(stageId);
				if (i < ranges.size()) {
					TimeRange range = ranges.get(i);
					conditions.addStage(stageId, range.getStart(), range.getEnd());
					currentRangesByStageId.put(stageId, range);
				}
			}

			List<Document> docs = itemDropDao.aggregateItemDrops(conditions);
			Map<String, List<Document>> docsGroupByStageId =
					docs.stream().collect(groupingBy(doc -> doc.getString("stageId")));
			for (String stageId : docsGroupByStageId.keySet()) {
				List<Document> docsForOneStage = docsGroupByStageId.get(stageId);
				TimeRange currentRange = currentRangesByStageId.get(stageId);
				Integer timesForStage = docsForOneStage.get(0).getInteger("times");
				Set<String> dropSet = dropInfoService.getDropSet(server, stageId, currentRange.getStart());
				Map<String, List<DropMatrixElement>> mapByItemId =
						mapByStageIdAndItemId.getOrDefault(stageId, new HashMap<>());

				docsForOneStage.forEach(doc -> {
					String itemId = doc.getString("itemId");
					if (!dropSet.contains(itemId))
						logger.warn("Item " + itemId + " is invalid in stage " + stageId);
					else {
						dropSet.remove(itemId);
						Integer quantity = doc.getInteger("quantity");
						Integer times = doc.getInteger("times");
						DropMatrixElement element = new DropMatrixElement(stageId, itemId, quantity, times,
								currentRange.getStart(), currentRange.getEnd());
						List<DropMatrixElement> elements = mapByItemId.getOrDefault(itemId, new ArrayList<>());
						elements.add(element);
						mapByItemId.put(itemId, elements);
					}
				});

				if (!dropSet.isEmpty()) {
					dropSet.forEach(itemId -> {
						if (itemIds == null || itemIds.isEmpty() || itemIds.contains(itemId)) {
							DropMatrixElement element = new DropMatrixElement(stageId, itemId, 0, timesForStage,
									currentRange.getStart(), currentRange.getEnd());
							List<DropMatrixElement> elements = mapByItemId.getOrDefault(itemId, new ArrayList<>());
							elements.add(element);
							mapByItemId.put(itemId, elements);
						}
					});
				}
				mapByStageIdAndItemId.put(stageId, mapByItemId);
			}
		}

		List<DropMatrixElement> result = new ArrayList<>();
		mapByStageIdAndItemId.forEach((stageId, mapByItemId) -> {
			mapByItemId.forEach((itemId, elements) -> {
				DropMatrixElement newElement = combineElements(elements);
				result.add(newElement);
			});
		});
		return result;
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
	public List<DropMatrixElement> generateSegmentedGlobalDropMatrixElementMap(Server server, Integer interval,
			Integer range) {
		Long end = System.currentTimeMillis();
		Long start = end - TimeUnit.DAYS.toMillis(range);
		List<DropMatrixElement> result =
				generateSegmentedDropMatrixElementMap(server, null, null, start, end, null, interval);
		LastUpdateTimeUtil
				.setCurrentTimestamp(LastUpdateMapKeyName.TREND_RESULT + "_" + server + "_" + interval + "_" + range);
		logger.info("generateSegmentedGlobalDropMatrixElementMap done in {} ms", System.currentTimeMillis() - end);
		return result;
	}

	private List<DropMatrixElement> generateSegmentedDropMatrixElementMap(Server server, String stageId,
			List<String> itemIds, Long start, Long end, List<String> userIDs, Integer interval) {
		if (start == null || end == null || start.compareTo(end) >= 0)
			return new ArrayList<>();
		Long intervalMillis = TimeUnit.DAYS.toMillis(interval);
		int sectionNum =
				new Double(Math.ceil(new Double((end - start) * 1.0 / intervalMillis).doubleValue())).intValue();
		if (sectionNum > systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.MAX_SECTION_NUM)) {
			logger.error("exceed max section num, now is " + sectionNum);
			return new ArrayList<>();
		}

		QueryConditions conditions = new QueryConditions();
		conditions.addStage(stageId, start, end);
		conditions.setInterval(interval);
		if (server != null)
			conditions.addServer(server);
		if (Optional.ofNullable(userIDs).map(list -> !list.isEmpty()).orElse(false)) {
			userIDs.forEach(userID -> conditions.addUserID(userID));
		}
		if (Optional.ofNullable(itemIds).map(list -> !list.isEmpty()).orElse(false)) {
			itemIds.forEach(itemId -> conditions.addItemId(itemId));
		}

		List<Document> docs = itemDropDao.aggregateItemDrops(conditions);

		Map<String, Map<String, List<DropMatrixElement>>> map = new HashMap<>();
		Map<String, Map<Integer, Integer>> timesMap = new HashMap<>();
		docs.forEach(doc -> {
			String stageIdInDoc = doc.getString("stageId");
			String itemId = doc.getString("itemId");
			Integer quantity = doc.getInteger("quantity");
			Integer times = doc.getInteger("times");
			Integer section = doc.getDouble("section").intValue();

			DropMatrixElement element =
					new DropMatrixElement(stageIdInDoc, itemId, quantity, times, new Long(section), null);

			Map<String, List<DropMatrixElement>> subMap = map.getOrDefault(stageIdInDoc, new HashMap<>());
			List<DropMatrixElement> elements = subMap.getOrDefault(itemId, new ArrayList<>());
			elements.add(element);
			subMap.put(itemId, elements);
			map.put(stageIdInDoc, subMap);

			Map<Integer, Integer> timesMapSubMap = timesMap.getOrDefault(stageIdInDoc, new HashMap<>());
			timesMapSubMap.put(section, times);
			timesMap.put(stageIdInDoc, timesMapSubMap);
		});

		map.forEach((stageIdInDoc, subMap) -> {
			Map<Integer, Integer> timesSubMap = timesMap.get(stageIdInDoc);
			subMap.forEach((itemId, elements) -> {
				Set<Integer> sectionSet = new HashSet<>();
				for (int i = 0; i < sectionNum; i++)
					sectionSet.add(i);
				elements.forEach(el -> sectionSet.remove(el.getStart().intValue()));
				sectionSet.forEach(section -> {
					Integer times =
							timesSubMap == null || !timesSubMap.containsKey(section) ? 0 : timesSubMap.get(section);
					DropMatrixElement newElement =
							new DropMatrixElement(stageIdInDoc, itemId, 0, times, new Long(section), null);
					elements.add(newElement);
				});
				elements.sort((e1, e2) -> e1.getStart().compareTo(e2.getStart()));
				elements.forEach(el -> {
					el.setStart(el.getStart() * intervalMillis + start);
					el.setEnd(el.getStart() + intervalMillis);
				});
			});
		});
		return map.values().stream().flatMap(m -> m.values().stream().flatMap(List::stream))
				.collect(Collectors.toList());
	}

	@Override
	public List<DropMatrixElement> refreshSegmentedGlobalDropMatrixElementMap(Server server, Integer interval,
			Integer range) {
		return generateSegmentedGlobalDropMatrixElementMap(server, interval, range);
	}

	@Override
	public List<DropMatrixElement> generateCustomDropMatrixElements(Server server, String stageId, List<String> itemIds,
			Long start, Long end, List<String> userIDs, Integer interval) {
		List<TimeRange> splittedRanges = timeRangeService.getSplittedTimeRanges(server, stageId, start, end);
		Map<String, List<TimeRange>> timeRangeMap = new HashMap<>();
		timeRangeMap.put(stageId, splittedRanges);
		if (interval == null)
			return generateDropMatrixElementsFromTimeRangeMapByStageId(server, timeRangeMap, itemIds, userIDs);
		else
			return generateSegmentedDropMatrixElementMap(server, stageId, itemIds, start, end, userIDs, interval);
	}

}
