package io.penguinstats.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

	Page<ItemDrop> getValidItemDropsByStageId(String stageId, Pageable pageable);

	// below is v2

	@Cacheable(value = "drop-matrix-v2", key = "'drop-matrix-v2_' + #server + '_' + (#isPast ? 'past' : 'current')",
			condition = "#filter == null && #userID == null", sync = true)
	List<DropMatrixElement> generateGlobalDropMatrixElements(Server server, String userID, boolean isPast);

	@CachePut(value = "drop-matrix-v2", key = "'drop-matrix-v2_' + #server + '_' + (#isPast ? 'past' : 'current')",
			condition = "#filter == null && #userID == null")
	List<DropMatrixElement> refreshGlobalDropMatrixElements(Server server, boolean isPast);

	@Cacheable(value = "all-segmented-drop-matrix-v2",
			key = "'all-segmented-drop-matrix-v2_' + #server + '_' + #interval + '_' + #range",
			condition = "#filter == null", sync = true)
	List<DropMatrixElement> generateSegmentedGlobalDropMatrixElements(Server server, Long interval, Long range);

	@CachePut(value = "all-segmented-drop-matrix-v2",
			key = "'all-segmented-drop-matrix-v2_' + #server + '_' + #interval + '_' + #range",
			condition = "#filter == null")
	List<DropMatrixElement> refreshSegmentedGlobalDropMatrixElements(Server server, Long interval, Long range);

	List<DropMatrixElement> generateCustomDropMatrixElements(Server server, String stageId, List<String> itemIds,
			Long start, Long end, List<String> userIDs, Long interval);

	@Cacheable(value = "no-expiry-map", key = "'total-stage-times_' + #server + (#range == null ? '' : ('_' + #range))",
			condition = "#filter == null", sync = true)
	Map<String, Integer> getTotalStageTimesMap(Server server, Long range);

	@CachePut(value = "no-expiry-map", key = "'total-stage-times_' + #server + (#range == null ? '' : ('_' + #range))",
			condition = "#filter == null")
	Map<String, Integer> refreshTotalStageTimesMap(Server server, Long range);

	@Cacheable(value = "no-expiry-map", key = "'total-item-quantities_' + #server", condition = "#filter == null",
			sync = true)
	Map<String, Integer> getTotalItemQuantitiesMap(Server server);

	@CachePut(value = "no-expiry-map", key = "'total-item-quantities_' + #server", condition = "#filter == null")
	Map<String, Integer> refreshTotalItemQuantitiesMap(Server server);

}
