package io.penguinstats.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import io.penguinstats.enums.Server;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.ItemDrop;

public interface ItemDropService {

	void saveItemDrop(ItemDrop itemDrop);

	void batchSaveItemDrops(Collection<ItemDrop> itemDrops);

	void deleteItemDrop(String userID, String itemDropId) throws Exception;

	void recallItemDrop(String userID, String itemDropHashId) throws Exception;

	List<ItemDrop> getAllItemDrops();

	Page<ItemDrop> getAllItemDrops(Pageable pageable);

	List<ItemDrop> getAllReliableItemDrops();

	Page<ItemDrop> getVisibleItemDropsByUserID(String userID, Pageable pageable);

	List<ItemDrop> getItemDropsByUserID(String userID);

	Map<String, List<Double>> getStageTimesMap(Criteria filter, boolean isWeighted);

	Map<String, Map<String, Double>> getQuantitiesMap(Criteria filter, boolean isWeighted);

	@Cacheable(value = "drop-matrix", key = "#isWeighted ? 'weighted' : 'not-weighted'", condition = "#filter == null")
	List<DropMatrixElement> generateDropMatrixElements(Criteria filter, boolean isWeighted);

	@CachePut(value = "drop-matrix", key = "#isWeighted ? 'weighted' : 'not-weighted'", condition = "#filter == null")
	List<DropMatrixElement> updateDropMatrixElements(Criteria filter, boolean isWeighted);

	Map<String, Integer> generateUploadCountMap(Criteria criteria);

	// below is v2

	@Cacheable(value = "drop-matrix-v2", key = "'drop-matrix-v2_' + #server",
			condition = "#filter == null && #userID == null", sync = true)
	List<DropMatrixElement> generateGlobalDropMatrixElements(Server server, String userID);

	@CachePut(value = "drop-matrix-v2", key = "'drop-matrix-v2_' + #server",
			condition = "#filter == null && #userID == null")
	List<DropMatrixElement> refreshGlobalDropMatrixElements(Server server);

	@Cacheable(value = "all-segmented-drop-matrix-v2",
			key = "'all-segmented-drop-matrix-v2_' + #server + '_' + #interval + '_' + #range",
			condition = "#filter == null", sync = true)
	List<DropMatrixElement> generateSegmentedGlobalDropMatrixElementMap(Server server, Long interval, Long range);

	@CachePut(value = "all-segmented-drop-matrix-v2",
			key = "'all-segmented-drop-matrix-v2_' + #server + '_' + #interval + '_' + #range",
			condition = "#filter == null")
	List<DropMatrixElement> refreshSegmentedGlobalDropMatrixElementMap(Server server, Long interval, Long range);

	List<DropMatrixElement> generateCustomDropMatrixElements(Server server, String stageId, List<String> itemIds,
			Long start, Long end, List<String> userIDs, Long interval);

}
