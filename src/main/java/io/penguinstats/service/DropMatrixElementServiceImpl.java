package io.penguinstats.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.constant.Constant.DefaultValue;
import io.penguinstats.constant.Constant.SystemPropertyKey;
import io.penguinstats.dao.DropMatrixElementDao;
import io.penguinstats.dao.ItemDropDao;
import io.penguinstats.enums.DropMatrixElementType;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.QueryConditions;
import io.penguinstats.model.TimeRange;
import io.penguinstats.util.DropMatrixElementUtil;
import io.penguinstats.util.misc.DirtyStages;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service("dropMatrixElementService")
public class DropMatrixElementServiceImpl implements DropMatrixElementService {

    @Autowired
    private DropMatrixElementDao dropMatrixElementDao;

    @Autowired
    private ItemDropDao itemDropDao;

    @Autowired
    private DropInfoService dropInfoService;

    @Autowired
    private TimeRangeService timeRangeService;

    @Autowired
    private SystemPropertyService systemPropertyService;

    @Autowired
    private DirtyStages dirtyStages;

    @Override
    public void batchSave(Collection<DropMatrixElement> elements) {
        dropMatrixElementDao.saveAll(elements);
    }

    @Override
    public void batchDelete(DropMatrixElementType type, Server server, Boolean isPast) {
        dropMatrixElementDao.deleteAllByIsPastAndServer(type, isPast, server);
    }

    @Override
    public void batchDeleteByStageId(DropMatrixElementType type, Server server, Boolean isPast, String stageId) {
        dropMatrixElementDao.deleteAllByIsPastAndServerAndStageId(type, isPast, server, stageId);
    }

    @Override
    public List<DropMatrixElement> getGlobalDropMatrixElements(Server server, boolean isPast) {
        return dropMatrixElementDao.queryByTypeAndIsPastAndServer(DropMatrixElementType.REGULAR, isPast, server);
    }

    @Override
    public List<DropMatrixElement> getGlobalTrendElements(Server server) {
        return dropMatrixElementDao.queryByTypeAndIsPastAndServer(DropMatrixElementType.TREND, null, server);
    }

    @Override
    public List<DropMatrixElement> generateGlobalDropMatrixElements(Server server, String userID, boolean isPast) {
        Long startTime = System.currentTimeMillis();

        Map<String, Map<String, List<String>>> latestMaxAccumulatableTimeRangesMap =
                timeRangeService.getLatestMaxAccumulatableTimeRangesMapByServer(server);

        Map<String, TimeRange> allTimeRangesMap = timeRangeService.getTimeRangeMap();

        int maxSize = 0;
        Map<String, List<Entry<String, List<String>>>> convertedMap = new HashMap<>();
        for (String stageId : latestMaxAccumulatableTimeRangesMap.keySet()) {
            if (userID == null && isPast) {
                Set<String> dirtyStageIds = dirtyStages.getStageIds(server);
                if (!dirtyStageIds.contains(stageId)) {
                    continue;
                }
            }

            Map<String, List<String>> timeRangeIDsMapItemId = latestMaxAccumulatableTimeRangesMap.get(stageId);
            Map<String, List<String>> itemIdsMapTimeRangeID = convertItemIdBasedTimeRangesToTimeRangeBasedItemIds(
                    timeRangeIDsMapItemId, allTimeRangesMap, isPast);
            convertedMap.put(stageId, new ArrayList<>(itemIdsMapTimeRangeID.entrySet()));
            int timeRangeNums = itemIdsMapTimeRangeID.keySet().size();
            if (maxSize < timeRangeNums)
                maxSize = timeRangeNums;
        }

        List<String> userIDs = userID != null ? Collections.singletonList(userID) : new ArrayList<>();
        Map<String, Map<String, List<DropMatrixElement>>> allElementsMap = new HashMap<>();

        for (int i = 0; i < maxSize; i++) {
            if (userID == null) {
                log.info("generateGlobalDropMatrixElements for server {}... ({}/{}), isPast = {}", server, i + 1,
                        maxSize, isPast);
            }
            Map<String, List<TimeRange>> timeRangeMap = new HashMap<>();
            for (String stageId : convertedMap.keySet()) {
                List<Entry<String, List<String>>> entries = convertedMap.get(stageId);
                if (i >= entries.size())
                    continue;
                Entry<String, List<String>> entry = entries.get(i);
                TimeRange range = allTimeRangesMap.get(entry.getKey());
                if (range != null) {
                    timeRangeMap.put(stageId, Collections.singletonList(range));
                }
            }
            List<DropMatrixElement> elements = generateDropMatrixElementsFromTimeRangeMapByStageId(server, timeRangeMap,
                    new ArrayList<>(), userIDs, isPast);

            for (String stageId : convertedMap.keySet()) {
                Map<String, List<DropMatrixElement>> subMap = allElementsMap.getOrDefault(stageId, new HashMap<>());
                List<Entry<String, List<String>>> entries = convertedMap.get(stageId);
                if (i >= entries.size())
                    continue;
                Entry<String, List<String>> entry = entries.get(i);
                Set<String> itemIdSet = new HashSet<>(entry.getValue());
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
                .flatMap(m -> m.values().stream().map(els -> DropMatrixElementUtil.combineElements(els)))
                .collect(toList());

        if (userID == null && isPast) {
            List<DropMatrixElement> pastElements = getGlobalDropMatrixElements(server, isPast);
            Map<String, DropMatrixElement> postElementsMap = pastElements.stream()
                    .collect(Collectors.toMap(el -> el.getStageId() + "_" + el.getItemId(), el -> el));
            result.forEach(el -> postElementsMap.put(el.getStageId() + "_" + el.getItemId(), el));
            result = new ArrayList<>(postElementsMap.values());
        }

        if (userID == null) {
            log.info("generateGlobalDropMatrixElements done in {} ms for server {}, isPast = {}",
                    System.currentTimeMillis() - startTime, server, isPast);
        }

        return result;
    }

    @Override
    public List<DropMatrixElement> generateDefaultSegmentedGlobalDropMatrixElements(Server server) {
        Long interval = systemPropertyService.getPropertyLongValue(SystemPropertyKey.DEFAULT_GLOBAL_TREND_INTERVAL,
                DefaultValue.DEFAULT_GLOBAL_TREND_INTERVAL);
        Long range = systemPropertyService.getPropertyLongValue(SystemPropertyKey.DEFAULT_GLOBAL_TREND_RANGE,
                DefaultValue.DEFAULT_GLOBAL_TREND_INTERVAL);
        return generateSegmentedGlobalDropMatrixElements(server, interval, range);
    }

    @Override
    public List<DropMatrixElement> generateSegmentedGlobalDropMatrixElements(Server server, Long interval, Long range) {
        Long end = System.currentTimeMillis();
        Long start = end - range;
        List<DropMatrixElement> result =
                generateSegmentedDropMatrixElements(server, null, null, start, end, null, interval);
        log.info("generateSegmentedGlobalDropMatrixElementMap done in {} ms for server {}",
                System.currentTimeMillis() - end, server);
        return result;
    }

    @Override
    public List<DropMatrixElement> generateCustomDropMatrixElements(Server server, String stageId, List<String> itemIds,
            Long start, Long end, List<String> userIDs, Long interval) {
        List<TimeRange> splittedRanges = timeRangeService.getSplittedTimeRanges(server, stageId, start, end);
        Map<String, List<TimeRange>> timeRangeMap = new HashMap<>();
        timeRangeMap.put(stageId, splittedRanges);
        if (interval == null)
            return generateDropMatrixElementsFromTimeRangeMapByStageId(server, timeRangeMap, itemIds, userIDs, null);
        else
            return generateSegmentedDropMatrixElements(server, stageId, itemIds, start, end, userIDs, interval);
    }

    private Map<String, List<String>> convertItemIdBasedTimeRangesToTimeRangeBasedItemIds(
            Map<String, List<String>> timeRangeIDsMapItemId, Map<String, TimeRange> timeRangeMap, boolean isPast) {
        Map<String, List<String>> result = new HashMap<>();
        timeRangeIDsMapItemId.forEach((itemId, timeRangeIDs) -> {
            for (String timeRangeID : timeRangeIDs) {
                TimeRange range = timeRangeMap.get(timeRangeID);
                if (range == null) {
                    continue;
                }
                boolean isCurrentTimeInRange = range.isIn(System.currentTimeMillis());
                if (isPast && isCurrentTimeInRange || !isPast && !isCurrentTimeInRange)
                    continue;
                List<String> itemIds = result.getOrDefault(timeRangeID, new ArrayList<>());
                itemIds.add(itemId);
                result.put(timeRangeID, itemIds);
            }
        });
        return result;
    }

    private List<DropMatrixElement> generateDropMatrixElementsFromTimeRangeMapByStageId(Server server,
            Map<String, List<TimeRange>> timeRangeMap, List<String> itemIds, List<String> userIDs, Boolean isPast) {
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
                    if (doc.containsKey("itemId")) {
                        String itemId = doc.getString("itemId");
                        if (!dropSet.contains(itemId))
                            log.warn("Item " + itemId + " is invalid in stage " + stageId);
                        else {
                            dropSet.remove(itemId);
                            Integer quantity = doc.getInteger("quantity");
                            Integer times = doc.getInteger("times");
                            DropMatrixElement element = new DropMatrixElement(DropMatrixElementType.REGULAR, stageId,
                                    itemId, quantity, times, currentRange.getStart(), currentRange.getEnd(), server,
                                    isPast, System.currentTimeMillis());
                            List<DropMatrixElement> elements = mapByItemId.getOrDefault(itemId, new ArrayList<>());
                            elements.add(element);
                            mapByItemId.put(itemId, elements);
                        }
                    }
                });

                if (!dropSet.isEmpty()) {
                    dropSet.forEach(itemId -> {
                        if (itemIds == null || itemIds.isEmpty() || itemIds.contains(itemId)) {
                            DropMatrixElement element = new DropMatrixElement(DropMatrixElementType.REGULAR, stageId,
                                    itemId, 0, timesForStage, currentRange.getStart(), currentRange.getEnd(), server,
                                    isPast, System.currentTimeMillis());
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
                DropMatrixElement newElement = DropMatrixElementUtil.combineElements(elements);
                result.add(newElement);
            });
        });
        return result;
    }

    private List<DropMatrixElement> generateSegmentedDropMatrixElements(Server server, String stageId,
            List<String> itemIds, Long start, Long end, List<String> userIDs, Long interval) {
        if (end == null)
            end = System.currentTimeMillis();
        if (start == null || start.compareTo(end) >= 0)
            return new ArrayList<>();
        int sectionNum = new Double(Math.ceil(new Double((end - start) * 1.0 / interval).doubleValue())).intValue();
        if (sectionNum > systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.MAX_SECTION_NUM,
                DefaultValue.MAX_SECTION_NUM)) {
            log.error("exceed max section num, now is " + sectionNum);
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
            if (doc.containsKey("itemId")) {
                String stageIdInDoc = doc.getString("stageId");
                String itemId = doc.getString("itemId");
                Integer quantity = doc.getInteger("quantity");
                Integer times = doc.getInteger("times");
                Integer section = doc.getDouble("section").intValue();

                DropMatrixElement element = new DropMatrixElement(DropMatrixElementType.TREND, stageIdInDoc, itemId,
                        quantity, times, new Long(section), null, server, null, System.currentTimeMillis());

                Map<String, List<DropMatrixElement>> subMap = map.getOrDefault(stageIdInDoc, new HashMap<>());
                List<DropMatrixElement> elements = subMap.getOrDefault(itemId, new ArrayList<>());
                elements.add(element);
                subMap.put(itemId, elements);
                map.put(stageIdInDoc, subMap);

                Map<Integer, Integer> timesMapSubMap = timesMap.getOrDefault(stageIdInDoc, new HashMap<>());
                timesMapSubMap.put(section, times);
                timesMap.put(stageIdInDoc, timesMapSubMap);
            }
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
                    DropMatrixElement newElement = new DropMatrixElement(DropMatrixElementType.TREND, stageIdInDoc,
                            itemId, 0, times, new Long(section), null, server, null, System.currentTimeMillis());
                    elements.add(newElement);
                });
                elements.sort((e1, e2) -> e1.getStart().compareTo(e2.getStart()));
                elements.forEach(el -> {
                    el.setStart(el.getStart() * interval + start);
                    el.setEnd(el.getStart() + interval);
                });
            });
        });
        return map.values().stream().flatMap(m -> m.values().stream().flatMap(List::stream))
                .collect(Collectors.toList());
    }

}
