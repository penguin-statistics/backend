package io.penguinstats.dao;

import java.util.List;

import io.penguinstats.model.DropMatrix;

public interface DropMatrixDao extends BaseDao<DropMatrix> {

	void batchSave(List<DropMatrix> list);

	void removeAll();

	void removeDropMatrix(String stageId, String itemId);

	void updateDropMatrix(DropMatrix dropMatrix);

	DropMatrix findDropMatrixByStageIdAndItemId(String stageId, String itemId);

	List<DropMatrix> findDropMatrixByStageId(String stageId);

	void increateTimesForOneStage(String stageId, Integer times);

	void increateQuantityForOneElement(String stageId, String itemId, Integer quantity);

}
