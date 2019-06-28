package io.penguinstats.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.penguinstats.bean.DropMatrix;
import io.penguinstats.bean.Stage;
import io.penguinstats.dao.DropMatrixDao;

public class DropMatrixService {

	private static final StageService stageService = StageService.getInstance();

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
	 * @Title: increateQuantityForOneElement
	 * @Description: Increase quantity for one element.
	 * @param stageId
	 * @param itemId
	 * @param quantity
	 * @return void
	 */
	public void increateQuantityForOneElement(String stageId, String itemId, Integer quantity) {
		dropMatrixDao.increateQuantityForOneElement(stageId, itemId, quantity);
	}

	/**
	 * @Title: increateTimesForOneStage
	 * @Description: Increase times for all records in the given stage.
	 * @param stageId
	 * @param times
	 * @return void
	 */
	public void increateTimesForOneStage(String stageId, Integer times) {
		dropMatrixDao.increateTimesForOneStage(stageId, times);
	}

	/**
	 * @Title: hasElementsForOneStage
	 * @Description: Return false if one stage has no elements at all.
	 * @param stageId
	 * @return boolean
	 */
	public boolean hasElementsForOneStage(String stageId) {
		return !dropMatrixDao.findElementsByStageId(stageId).isEmpty();
	}

	/**
	 * @Title: initializeElementsForOneStage
	 * @Description: Create an element for each drop item in one stage with quantity = 0 and times = 0 in the matrix.
	 * @param stageId
	 * @return void
	 */
	public boolean initializeElementsForOneStage(String stageId) {
		Stage stage = stageService.getStage(stageId);
		if (stage == null)
			return false;
		Set<String> dropSet = stage.getDropsSet();
		List<DropMatrix> list = new ArrayList<>();
		for (String itemId : dropSet)
			list.add(new DropMatrix(stageId, itemId, 0, 0));
		dropMatrixDao.batchSave(list);
		return true;
	}

}
