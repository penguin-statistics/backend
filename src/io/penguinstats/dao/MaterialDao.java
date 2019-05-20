package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

import io.penguinstats.bean.Material;

public class MaterialDao extends BaseDao<Material> {

	public MaterialDao() {
		super("material");
	}

	public Material findByID(int id) {
		MongoCursor<Document> iter = collection.find(eq("id", new Document().append("id", id))).iterator();
		if (iter.hasNext()) {
			Document document = iter.next();
			return new Material(document);
		}
		return null;
	}

}
