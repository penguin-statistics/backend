package io.penguinstats.service;

import io.penguinstats.model.DropMatrix;
import io.penguinstats.model.ItemDrop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;

public interface ItemDropService {

	void saveItemDrop(ItemDrop itemDrop);

	void deleteItemDrop(String userId, String itemDropId) throws Exception;

	List<ItemDrop> getAllItemDrops();

	List<ItemDrop> getAllReliableItemDrops();

	Page<ItemDrop> getVisibleItemDropsByUserID(String userID, Pageable pageable);

	List<ItemDrop> getItemDropsByUserID(String userID);

	Map<String, List<Integer>> getStageTimesMap(Criteria filter);

	Map<String, Map<String, Integer>> getQuantitiesMap(Criteria filter);

	List<DropMatrix> generateDropMatrixList(Criteria filter);

	Map<String, Map<String, DropMatrix>> generateDropMatrixMap(Criteria filter);

}
