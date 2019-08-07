package io.penguinstats.service;

import io.penguinstats.dao.DropMatrixDao;
import io.penguinstats.model.DropMatrix;
import io.penguinstats.model.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("dropMatrixService")
public class DropMatrixServiceImpl implements DropMatrixService {

	@Autowired
	private StageService stageService;

	@Autowired
	private DropMatrixDao dropMatrixDao;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<DropMatrix> getAllElements() {
		return dropMatrixDao.findAll();
	}

	@Override
	public void saveDropMatrix(DropMatrix dropMatrix) {
		dropMatrixDao.save(dropMatrix);
	}

	/**
	 * @Title: clearAndUpdateAll
	 * @Description: REMOVE all elements in the table and save new elements.
	 * @param elements
	 * @return void
	 */
	@Override
	public void clearAndUpdateAll(List<DropMatrix> elements) {
		dropMatrixDao.deleteAll();
		dropMatrixDao.saveAll(elements);
	}

	/**
	 * @Title: increaseQuantityForOneElement
	 * @Description: Increase quantity for one element.
	 * @param stageId
	 * @param itemId
	 * @param quantity
	 * @return void
	 */
	@Override
	public void increaseQuantityForOneElement(String stageId, String itemId, Integer quantity) {
		Query query = new Query(
				new Criteria().andOperator(Criteria.where("stageId").is(stageId), Criteria.where("itemId").is(itemId)));
		Update update = new Update().inc("quantity", quantity);
		mongoTemplate.updateFirst(query, update, DropMatrix.class);
	}

	/**
	 * @Title: increaseTimesForOneStage
	 * @Description: Increase times for all records in the given stage.
	 * @param stageId
	 * @param times
	 * @return void
	 */
	@Override
	public void increaseTimesForOneStage(String stageId, Integer times) {
		Query query = new Query(Criteria.where("stageId").is(stageId));
		Update update = new Update().inc("times", times);
		mongoTemplate.updateMulti(query, update, DropMatrix.class);
	}

	/**
	 * @Title: hasElementsForOneStage
	 * @Description: Return false if one stage has no elements at all.
	 * @param stageId
	 * @return boolean
	 */
	@Override
	public boolean hasElementsForOneStage(String stageId) {
		return !dropMatrixDao.findDropMatrixByStageId(stageId).isEmpty();
	}

	/**
	 * @Title: initializeElementsForOneStage
	 * @Description: Create an element for each drop item in one stage with quantity = 0 and times = 0 in the matrix.
	 * @param stageId
	 * @return void
	 */
	@Override
	public boolean initializeElementsForOneStage(String stageId) {
		Stage stage = stageService.getStageByStageId(stageId);
		if (stage == null)
			return false;
		Set<String> dropSet = stage.getDropsSet();
		List<DropMatrix> list = new ArrayList<>();
		for (String itemId : dropSet)
			list.add(new DropMatrix(stageId, itemId, 0, 0));
		dropMatrixDao.saveAll(list);
		return true;
	}

}
