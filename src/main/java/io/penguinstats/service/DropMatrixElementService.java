package io.penguinstats.service;

import java.util.Collection;
import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import io.penguinstats.enums.DropMatrixElementType;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropMatrixElement;

public interface DropMatrixElementService {

	void batchSave(Collection<DropMatrixElement> elements);

	void batchDelete(DropMatrixElementType type, Server server, Boolean isPast);

	@Cacheable(value = "drop-matrix-v2", key = "'drop-matrix-v2_' + #server + '_' + (#isPast ? 'past' : 'current')",
			sync = true)
	List<DropMatrixElement> getGlobalDropMatrixElements(Server server, boolean isPast);

	@Cacheable(value = "all-segmented-drop-matrix-v2", key = "'all-segmented-drop-matrix-v2_' + #server", sync = true)
	List<DropMatrixElement> getGlobalTrendElements(Server server);

	@CachePut(value = "drop-matrix-v2", key = "'drop-matrix-v2_' + #server + '_' + (#isPast ? 'past' : 'current')",
			condition = "#userID == null")
	List<DropMatrixElement> generateGlobalDropMatrixElements(Server server, String userID, boolean isPast);

	@CachePut(value = "all-segmented-drop-matrix-v2", key = "'all-segmented-drop-matrix-v2_' + #server")
	List<DropMatrixElement> generateDefaultSegmentedGlobalDropMatrixElements(Server server);

	@CachePut(value = "all-segmented-drop-matrix-v2",
			key = "'all-segmented-drop-matrix-v2_' + #server + '_' + #interval + '_' + #range")
	List<DropMatrixElement> generateSegmentedGlobalDropMatrixElements(Server server, Long interval, Long range);

	List<DropMatrixElement> generateCustomDropMatrixElements(Server server, String stageId, List<String> itemIds,
			Long start, Long end, List<String> userIDs, Long interval);

}
