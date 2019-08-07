package io.penguinstats.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.ItemDao;
import io.penguinstats.model.Item;
import io.penguinstats.util.LastUpdateTimeUtil;

@Service("itemService")
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemDao itemDao;

	@Override
	public void saveItem(Item item) {
		itemDao.save(item);
	}

	@Override
	public Item getItemByItemId(String itemId) {
		return itemDao.findByItemId(itemId);
	}

	/**
	 * @Title: getAllItems
	 * @Description: Return all items in the database as a list.
	 * @return List<Item>
	 */
	@Override
	public List<Item> getAllItems() {
		List<Item> items = itemDao.findAll();
		LastUpdateTimeUtil.setCurrentTimestamp("itemList");
		return items;
	}

	/**
	 * @Title: getItemMap
	 * @Description: Return a map which has itemId as key and item object as value.
	 * @return Map<String,Item>
	 */
	@Override
	public Map<String, Item> getItemMap() {
		List<Item> list = getAllItems();
		Map<String, Item> map = new HashMap<>();
		list.forEach(item -> map.put(item.getItemId(), item));
		return map;
	}

}
