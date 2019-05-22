package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

import io.penguinstats.bean.StageTimes;

public class StageTimesDao extends BaseDao<StageTimes> {

	public StageTimesDao() {
		super("stage_times");
	}

	public StageTimes findByStageIDAndStageType(int stageID, String stageType) {
		MongoCursor<Document> iter =
				collection.find(and(eq("stageID", stageID), eq("stageType", stageType))).iterator();
		if (iter.hasNext()) {
			Document document = iter.next();
			return new StageTimes(document);
		}
		return null;
	}

	public Document findDocumentByStageIDAndStageType(int stageID, String stageType) {
		MongoCursor<Document> iter =
				collection.find(and(eq("stageID", stageID), eq("stageType", stageType))).iterator();
		if (iter.hasNext()) {
			Document document = iter.next();
			return document;
		}
		return null;
	}

}
