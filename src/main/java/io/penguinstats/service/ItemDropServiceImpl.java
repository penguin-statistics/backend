package io.penguinstats.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.dao.ItemDropDao;
import io.penguinstats.enums.ErrorCode;
import io.penguinstats.enums.Server;
import io.penguinstats.model.ItemDrop;
import io.penguinstats.model.QueryConditions;
import io.penguinstats.util.HashUtil;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.penguinstats.util.exception.BusinessException;

@Service("itemDropService")
public class ItemDropServiceImpl implements ItemDropService {

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
            throw new BusinessException(ErrorCode.NOT_FOUND,
                    "ItemDrop[" + itemDropId + "] not found for user with ID[" + userID + "]", Optional.empty());
        }

        itemDrop.setIsDeleted(true);
        itemDropDao.save(itemDrop);
    }

    @Override
    public void recallItemDrop(String userID, String itemDropHashId) throws Exception {
        Pageable pageable = PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "timestamp"));
        List<ItemDrop> itemDropList = getVisibleItemDropsByUserID(userID, pageable).getContent();
        if (itemDropList.size() == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND,
                    "Visible ItemDrop not found for user with ID[" + userID + "]", Optional.empty());
        }

        ItemDrop lastItemDrop = itemDropList.get(0);
        String lastItemDropHashId = HashUtil.getHash(lastItemDrop.getId().toString());
        if (!lastItemDropHashId.equals(itemDropHashId)) {
            throw new BusinessException(ErrorCode.ITEM_DROP_HASH_ID_NOT_MATCH, "ItemDropHashId doesn't match!",
                    Optional.ofNullable(itemDropHashId));
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

    @Override
    public Page<ItemDrop> getValidItemDropsByStageId(String stageId, Pageable pageable) {
        return itemDropDao.findValidItemDropByStageId(stageId, pageable);
    }

    @Override
    public List<ItemDrop> getItemDropsByMD5(String md5) {
        return itemDropDao.findByMD5(md5);
    }

    @Override
    public Map<String, Integer> getTotalStageTimesMap(Server server, Long range) {
        QueryConditions conditions = new QueryConditions().addServer(server).setRange(range);
        List<Document> docs = itemDropDao.aggregateStageTimes(conditions);
        Map<String, Integer> result =
                docs.stream().collect(Collectors.toMap(doc -> doc.getString("_id"), doc -> doc.getInteger("times")));
        LastUpdateTimeUtil.setCurrentTimestamp(
                LastUpdateMapKeyName.TOTAL_STAGE_TIMES_MAP + "_" + server + (range == null ? "" : "_" + range));
        return result;
    }

    @Override
    public Map<String, Integer> refreshTotalStageTimesMap(Server server, Long range) {
        return getTotalStageTimesMap(server, range);
    }

    @Override
    public Map<String, Integer> getTotalItemQuantitiesMap(Server server) {
        QueryConditions conditions = new QueryConditions().addServer(server);
        List<Document> docs = itemDropDao.aggregateItemQuantities(conditions);
        // TODO: Obsidian related items are excluded here using hardcode
        Map<String, Integer> result = docs.stream().filter(doc -> !doc.getString("_id").contains("Obsidian"))
                .collect(Collectors.toMap(doc -> doc.getString("_id"), doc -> doc.getInteger("quantity")));
        LastUpdateTimeUtil.setCurrentTimestamp(LastUpdateMapKeyName.TOTAL_ITEM_QUANTITIES_MAP + "_" + server);
        return result;
    }

    @Override
    public Map<String, Integer> refreshTotalItemQuantitiesMap(Server server) {
        return getTotalItemQuantitiesMap(server);
    }

}
