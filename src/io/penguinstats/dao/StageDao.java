package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

import io.penguinstats.bean.Stage;

public class StageDao extends BaseDao<Stage> {

	public StageDao() {
		super("stage");
	}

	public Stage findByID(int id) {
		MongoCursor<Document> iter = collection.find(eq("id", new Document().append("id", id))).iterator();
		if (iter.hasNext()) {
			Document document = iter.next();
			return new Stage(document);
		}
		return null;
	}

}
