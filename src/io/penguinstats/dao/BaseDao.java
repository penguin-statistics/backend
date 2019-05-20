package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.eq;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import io.penguinstats.bean.Documentable;

public abstract class BaseDao<T extends Documentable> {

	private static Logger logger = LogManager.getLogger(BaseDao.class);
	private static final String DB_NAME = "penguin_stats";

	protected MongoConnection conn = null;
	protected MongoDatabase db = null;
	protected MongoCollection<Document> collection = null;

	public BaseDao(String collectionName) {
		this.conn = MongoConnection.getInstance();
		this.conn.init();
		this.db = conn.getMongo().getDatabase(DB_NAME);
		this.collection = this.db.getCollection(collectionName);
	}

	public boolean save(T t) {
		try {
			collection.insertOne(t.toDocument());
			return true;
		} catch (Exception e) {
			logger.error("Error in save", e);
			return false;
		}
	}

	public boolean updateDocument(Document doc) {
		try {
			collection.replaceOne(eq("_id", doc.getObjectId("_id")), doc);
			return true;
		} catch (Exception e) {
			logger.error("Error in updateDocument", e);
			return false;
		}
	}

	public List<Document> findAllDocuments() {
		MongoCursor<Document> iter = collection.find().iterator();
		List<Document> list = new ArrayList<>();
		while (iter.hasNext()) {
			Document document = iter.next();
			list.add(document);
		}
		return list;
	}

	public List<T> findAll() {
		MongoCursor<Document> iter = collection.find().iterator();
		List<T> list = new ArrayList<>();
		while (iter.hasNext()) {
			Document document = iter.next();
			list.add(fromDocument(document));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private T fromDocument(Document document) {
		Class<T> classType =
				(Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		try {
			Constructor<T> constructor = classType.getConstructor(Document.class);
			T t = constructor.newInstance(document);
			return t;
		} catch (Exception e) {
			logger.error("Error in fromDocument", e);
			return null;
		}
	}

}
