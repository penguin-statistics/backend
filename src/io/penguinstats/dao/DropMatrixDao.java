package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

import io.penguinstats.bean.DropMatrix;

public class DropMatrixDao extends BaseDao<DropMatrix> {

	public DropMatrixDao() {
		super("drop_matrix");
	}

	public DropMatrix findByStageIDAndStageTypeAndItemID(int stageID, String stageType, int itemID) {
		MongoCursor<Document> iter = collection
				.find(and(eq("stageID", stageID), eq("stageType", stageType), eq("itemID", itemID))).iterator();
		if (iter.hasNext()) {
			Document document = iter.next();
			return new DropMatrix(document);
		}
		return null;
	}

	public Document findDocumentByStageIDAndStageTypeAndItemID(int stageID, String stageType, int itemID) {
		MongoCursor<Document> iter = collection
				.find(and(eq("stageID", stageID), eq("stageType", stageType), eq("itemID", itemID))).iterator();
		if (iter.hasNext()) {
			Document document = iter.next();
			return document;
		}
		return null;
	}

}
