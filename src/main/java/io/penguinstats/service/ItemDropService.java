package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Criteria;

import io.penguinstats.model.DropMatrix;
import io.penguinstats.model.ItemDrop;

public interface ItemDropService {

	void saveItemDrop(ItemDrop itemDrop);

	List<ItemDrop> getAllItemDrops();

	public List<ItemDrop> getAllReliableItemDrops();

	Map<String, List<Integer>> getStageTimesMap(Criteria filter);

	Map<String, Map<String, Integer>> getQuantitiesMap(Criteria filter);

	List<DropMatrix> generateDropMatrixList(Criteria filter);

	Map<String, Map<String, DropMatrix>> generateDropMatrixMap(Criteria filter);

}
