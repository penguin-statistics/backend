package io.penguinstats.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.DropMatrixDao;
import io.penguinstats.model.DropMatrix;
import io.penguinstats.model.Stage;

@Service("dropMatrixService")
public class DropMatrixServiceImpl implements DropMatrixService {

	@Autowired
	private StageService stageService;

	@Autowired
	private DropMatrixDao dropMatrixDao;

	public List<DropMatrix> getAllElements() {
		return dropMatrixDao.findAll();
	}

	public void saveDropMatrix(DropMatrix dropMatrix) {
		dropMatrixDao.save(dropMatrix);
	}

	/**
	 * @Title: clearAndUpdateAll
	 * @Description: REMOVE all elements in the table and save new elements.
	 * @param elements
	 * @return void
	 */
	public void clearAndUpdateAll(List<DropMatrix> elements) {
		dropMatrixDao.removeAll();
		dropMatrixDao.batchSave(elements);
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
		return !dropMatrixDao.findDropMatrixByStageId(stageId).isEmpty();
	}

	/**
	 * @Title: initializeElementsForOneStage
	 * @Description: Create an element for each drop item in one stage with quantity = 0 and times = 0 in the matrix.
	 * @param stageId
	 * @return void
	 */
	public boolean initializeElementsForOneStage(String stageId) {
		Stage stage = stageService.getStageByStageId(stageId);
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
