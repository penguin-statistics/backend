package io.penguinstats.service;

import java.util.List;

import io.penguinstats.model.DropMatrix;

public interface DropMatrixService {

	List<DropMatrix> getAllElements();

	void saveDropMatrix(DropMatrix dropMatrix);

	void clearAndUpdateAll(List<DropMatrix> elements);

	void increateQuantityForOneElement(String stageId, String itemId, Integer quantity);

	void increateTimesForOneStage(String stageId, Integer times);

	boolean hasElementsForOneStage(String stageId);

	boolean initializeElementsForOneStage(String stageId);

}
