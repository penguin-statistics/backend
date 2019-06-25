package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCursor;

import io.penguinstats.bean.DropMatrix;

public class DropMatrixDao extends BaseDao<DropMatrix> {

	private static Logger logger = LogManager.getLogger(DropMatrixDao.class);

	public DropMatrixDao() {
		super("drop_matrix_v2");
	}

	public DropMatrix findElement(String stageId, String itemId) {
		MongoCursor<Document> iter = collection.find(and(eq("stageId", stageId), eq("itemId", itemId))).iterator();
		if (iter.hasNext()) {
			Document document = iter.next();
			return new DropMatrix(document);
		}
		return null;
	}

	public List<DropMatrix> findElementsByStageId(String stageId) {
		List<DropMatrix> list = new ArrayList<>();
		MongoCursor<Document> iter = collection.find(eq("stageId", stageId)).iterator();
		while (iter.hasNext()) {
			try {
				list.add(new DropMatrix(iter.next()));
			} catch (Exception e) {
				logger.error("Error in findElementsByStageId", e);
			}
		}
		return list;
	}

	public void increateTimesForOneStage(String stageId, Integer times) {
		collection.updateMany(eq("stageId", stageId), inc("times", times));
	}

	public void increateQuantityForOneElement(String stageId, String itemId, Integer quantity) {
		collection.updateMany(and(eq("stageId", stageId), eq("itemId", itemId)), inc("quantity", quantity));
	}

}
