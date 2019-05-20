package io.penguinstats.service;

import java.util.List;

import io.penguinstats.bean.ItemDrop;
import io.penguinstats.dao.ItemDropDao;

public class ItemDropService {

	private static ItemDropService instance = new ItemDropService();
	private static ItemDropDao dao = new ItemDropDao();

	private ItemDropService() {}

	public static ItemDropService getInstance() {
		return instance;
	}

	public boolean saveItemDrop(ItemDrop itemDrop) {
		return dao.save(itemDrop);
	}

	public List<ItemDrop> getAllItemDrops() {
		return dao.findAll();
	}

}
