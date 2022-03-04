package io.penguinstats.service;

import java.util.Collection;
import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import io.penguinstats.constant.Constant.CacheValue;
import io.penguinstats.enums.DropMatrixElementType;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropMatrixElement;

public interface DropMatrixElementService {

    void batchSave(Collection<DropMatrixElement> elements);

    void batchDelete(DropMatrixElementType type, Server server, Boolean isPast);

    void batchDeleteByStageId(DropMatrixElementType type, Server server, Boolean isPast, String stageId);

    @Cacheable(value = CacheValue.DROP_MATRIX, key = "#server + '_' + (#isPast ? 'past' : 'current')", sync = true)
    List<DropMatrixElement> getGlobalDropMatrixElements(Server server, boolean isPast);

    @Cacheable(value = CacheValue.SEGMENTED_DROP_MATRIX, key = "#server", sync = true)
    List<DropMatrixElement> getGlobalTrendElements(Server server);

    @CachePut(value = CacheValue.DROP_MATRIX, key = "#server + '_' + (#isPast ? 'past' : 'current')",
            condition = "#userID == null")
    List<DropMatrixElement> generateGlobalDropMatrixElements(Server server, String userID, boolean isPast);

    @CachePut(value = CacheValue.SEGMENTED_DROP_MATRIX, key = "#server")
    List<DropMatrixElement> generateDefaultSegmentedGlobalDropMatrixElements(Server server);

    @CachePut(value = CacheValue.SEGMENTED_DROP_MATRIX, key = "#server + '_' + #interval + '_' + #range")
    List<DropMatrixElement> generateSegmentedGlobalDropMatrixElements(Server server, Long interval, Long range);

    List<DropMatrixElement> generateCustomDropMatrixElements(Server server, String stageId, List<String> itemIds,
            Long start, Long end, List<String> userIDs, Long interval);

}
