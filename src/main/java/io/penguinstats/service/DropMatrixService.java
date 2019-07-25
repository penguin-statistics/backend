package io.penguinstats.service;

import io.penguinstats.model.DropMatrix;

import java.util.List;

public interface DropMatrixService {

	List<DropMatrix> getAllElements();

	void saveDropMatrix(DropMatrix dropMatrix);

	void clearAndUpdateAll(List<DropMatrix> elements);

	void increaseQuantityForOneElement(String stageId, String itemId, Integer quantity);

	void increaseTimesForOneStage(String stageId, Integer times);

	boolean hasElementsForOneStage(String stageId);

	boolean initializeElementsForOneStage(String stageId);

}
