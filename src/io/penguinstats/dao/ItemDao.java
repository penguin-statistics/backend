package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

import io.penguinstats.bean.Item;

public class ItemDao extends BaseDao<Item> {

	public ItemDao() {
		super("item");
	}

	/**
	 * @Title: findByItemId
	 * @Description: Retrieve item using itemId
	 * @param itemId
	 * @return Item
	 */
	public Item findByItemId(String itemId) {
		MongoCursor<Document> iter = collection.find(eq("itemId", itemId)).iterator();
		if (iter.hasNext()) {
			Document document = iter.next();
			return new Item(document);
		}
		return null;
	}

}
