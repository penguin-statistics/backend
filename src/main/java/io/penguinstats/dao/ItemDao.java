package io.penguinstats.dao;

import io.penguinstats.model.Item;

public interface ItemDao extends BaseDao<Item> {

	void removeItem(String itemId);

	void updateItem(Item item);

	Item findItemByItemId(String itemId);

}
