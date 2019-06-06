package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

import io.penguinstats.bean.ItemDrop;

public class ItemDropDao extends BaseDao<ItemDrop> {

	public ItemDropDao() {
		super("item_drop_v2");
	}

	/**
	 * @Title: findAllReliableItemDrops
	 * @Description: Return a list of all reliable item drop records.
	 * @return List<ItemDrop>
	 */
	public List<ItemDrop> findAllReliableItemDrops() {
		List<ItemDrop> itemDrops = new ArrayList<>();
		MongoCursor<Document> iter = collection.find(eq("isReliable", true)).iterator();
		while (iter.hasNext()) {
			Document document = iter.next();
			itemDrops.add(new ItemDrop(document));
		}
		return itemDrops;
	}

}
