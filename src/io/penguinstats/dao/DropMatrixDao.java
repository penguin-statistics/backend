package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

import io.penguinstats.bean.DropMatrix;

public class DropMatrixDao extends BaseDao<DropMatrix> {

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

	public void increateTimesForOneStage(String stageId, Integer times) {
		collection.updateMany(eq("stageId", stageId), inc("times", times));
	}

	public void increateQuantityForOneElement(String stageId, String itemId, Integer quantity) {
		collection.updateMany(and(eq("stageId", stageId), eq("itemId", itemId)), inc("quantity", quantity));
	}

}
