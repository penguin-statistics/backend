package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

import io.penguinstats.bean.Chapter;

public class ChapterDao extends BaseDao<Chapter> {

	public ChapterDao() {
		super("chapter");
	}

	public Chapter findByID(int id) {
		MongoCursor<Document> iter = collection.find(eq("id", id)).iterator();
		if (iter.hasNext()) {
			Document document = iter.next();
			return new Chapter(document);
		}
		return null;
	}

}
