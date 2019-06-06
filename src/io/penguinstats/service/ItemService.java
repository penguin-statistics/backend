package io.penguinstats.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.penguinstats.bean.Item;
import io.penguinstats.dao.ItemDao;

public class ItemService {

	private static ItemService instance = new ItemService();
	private static ItemDao itemDao = new ItemDao();

	private ItemService() {}

	public static ItemService getInstance() {
		return instance;
	}

	public boolean saveItem(Item item) {
		return itemDao.save(item);
	}

	public Item getItem(String itemId) {
		return itemDao.findByItemId(itemId);
	}

	/**
	 * @Title: getAllItems
	 * @Description: Return all items in the database as a list.
	 * @return List<Item>
	 */
	public List<Item> getAllItems() {
		return itemDao.findAll();
	}

	/**
	 * @Title: getItemMap
	 * @Description: Return a map which has itemId as key and item object as value.
	 * @return Map<String,Item>
	 */
	public Map<String, Item> getItemMap() {
		List<Item> list = getAllItems();
		Map<String, Item> map = new HashMap<>();
		list.forEach(item -> map.put(item.getItemId(), item));
		return map;
	}

}
