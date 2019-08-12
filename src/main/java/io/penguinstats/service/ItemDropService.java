package io.penguinstats.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import io.penguinstats.model.DropMatrix;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.ItemDrop;

public interface ItemDropService {

	void saveItemDrop(ItemDrop itemDrop);

	void batchSaveItemDrops(Collection<ItemDrop> itemDrops);

	void deleteItemDrop(String userID, String itemDropId) throws Exception;

	void recallItemDrop(String userID, String itemDropHashId) throws Exception;

	List<ItemDrop> getAllItemDrops();

	List<ItemDrop> getAllReliableItemDrops();

	Page<ItemDrop> getVisibleItemDropsByUserID(String userID, Pageable pageable);

	List<ItemDrop> getItemDropsByUserID(String userID);

	Map<String, List<Double>> getStageTimesMap(Criteria filter, boolean isWeighted);

	Map<String, Map<String, Double>> getQuantitiesMap(Criteria filter, boolean isWeighted);

	List<DropMatrix> generateDropMatrixList(Criteria filter);

	@Cacheable(value = "drop-matrix", key = "#isWeighted ? 'weighted' : 'not-weighted'", condition = "#filter == null")
	List<DropMatrixElement> generateDropMatrixElements(Criteria filter, boolean isWeighted);

	Map<String, List<DropMatrixElement>> generateDropMatrixElements(Criteria filter, long interval, String stageId,
			String itemId);

	Map<String, Map<String, DropMatrix>> generateDropMatrixMap(Criteria filter);

	Map<String, Map<String, DropMatrixElement>> generateDropMatrixMap(Criteria filter, boolean isWeighted);

	Map<String, Integer> generateUploadCountMap(Criteria criteria);

	Long getMinTimestamp(String stageId);

}
