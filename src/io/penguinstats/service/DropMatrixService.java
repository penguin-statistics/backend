package io.penguinstats.service;

import java.util.List;

import io.penguinstats.bean.DropMatrix;
import io.penguinstats.dao.DropMatrixDao;

public class DropMatrixService {

	private static DropMatrixService instance = new DropMatrixService();
	private static DropMatrixDao dropMatrixDao = new DropMatrixDao();

	private DropMatrixService() {}

	public static DropMatrixService getInstance() {
		return instance;
	}

	public List<DropMatrix> getAllElements() {
		return dropMatrixDao.findAll();
	}

	public boolean saveDropMatrix(DropMatrix dropMatrix) {
		return dropMatrixDao.save(dropMatrix);
	}

	/**
	 * @Title: clearAndUpdateAll
	 * @Description: REMOVE all elements in the table and save new elements.
	 * @param elements
	 * @return boolean
	 */
	public boolean clearAndUpdateAll(List<DropMatrix> elements) {
		boolean deleteResult = dropMatrixDao.batchDelete();
		if (!deleteResult)
			return false;
		return dropMatrixDao.batchSave(elements);
	}

	/**
	 * @Title: addDrop
	 * @Description: One drop record comes and update quantity for one element and update times for a row.
	 * @param stageId
	 * @param itemId
	 * @param times
	 * @param quantity
	 */
	public void addDrop(String stageId, String itemId, Integer times, Integer quantity) {
		DropMatrix dropMatrix = dropMatrixDao.findElement(stageId, itemId);
		if (dropMatrix == null) {
			dropMatrix = new DropMatrix(stageId, itemId, 0, 0);
			dropMatrixDao.save(dropMatrix);
		}
		dropMatrixDao.increateQuantityForOneElement(stageId, itemId, quantity);
		dropMatrixDao.increateTimesForOneStage(stageId, times);
	}

}
