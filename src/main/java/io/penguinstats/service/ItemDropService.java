package io.penguinstats.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import io.penguinstats.model.DropMatrix;
import io.penguinstats.model.ItemDrop;

public interface ItemDropService {

	void saveItemDrop(ItemDrop itemDrop);

	void batchSaveItemDrops(Collection<ItemDrop> itemDrops);

	void deleteItemDrop(String userId, String itemDropId) throws Exception;

	List<ItemDrop> getAllItemDrops();

	List<ItemDrop> getAllReliableItemDrops();

	Page<ItemDrop> getVisibleItemDropsByUserID(String userID, Pageable pageable);

	List<ItemDrop> getItemDropsByUserID(String userID);

	Map<String, List<Integer>> getStageTimesMap(Criteria filter);

	Map<String, Map<String, Integer>> getQuantitiesMap(Criteria filter);

	List<DropMatrix> generateDropMatrixList(Criteria filter);

	Map<String, Map<String, DropMatrix>> generateDropMatrixMap(Criteria filter);

	Map<String, Integer> generateUploadCountMap(Criteria criteria);

}
