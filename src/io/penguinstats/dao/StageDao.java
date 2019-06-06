package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

import io.penguinstats.bean.Stage;

public class StageDao extends BaseDao<Stage> {

	public StageDao() {
		super("stage_v2");
	}

	/**
	 * @Title: findByStageId
	 * @Description: Retrieve stage using stageId
	 * @param stageId
	 * @return Stage
	 */
	public Stage findByStageId(String stageId) {
		MongoCursor<Document> iter = collection.find(eq("stageId", stageId)).iterator();
		if (iter.hasNext()) {
			Document document = iter.next();
			return new Stage(document);
		}
		return null;
	}

}
