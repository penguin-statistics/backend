package io.penguinstats.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.ItemDao;
import io.penguinstats.model.Item;

@Service("itemService")
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemDao itemDao;

	public void saveItem(Item item) {
		itemDao.save(item);
	}

	public Item getItemByItemId(String itemId) {
		return itemDao.findItemByItemId(itemId);
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
