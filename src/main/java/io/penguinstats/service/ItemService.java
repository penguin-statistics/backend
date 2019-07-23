package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import io.penguinstats.model.Item;

public interface ItemService {

	void saveItem(Item item);

	Item getItemByItemId(String itemId);

	List<Item> getAllItems();

	Map<String, Item> getItemMap();

}
