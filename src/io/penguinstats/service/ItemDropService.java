package io.penguinstats.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.penguinstats.bean.Drop;
import io.penguinstats.bean.DropMatrix;
import io.penguinstats.bean.Item;
import io.penguinstats.bean.ItemDrop;
import io.penguinstats.dao.ItemDropDao;

public class ItemDropService {

	private static final ItemDropDao itemDropDao = new ItemDropDao();
	private static final ItemService itemService = ItemService.getInstance();

	private static ItemDropService instance = new ItemDropService();
	private static Logger logger = LogManager.getLogger(ItemDropService.class);

	public static final Long[] ADD_TIME_POINTS = new Long[] {0L, 1558989300000L, 1560045456000L};

	private ItemDropService() {}

	public static ItemDropService getInstance() {
		return instance;
	}

	public boolean saveItemDrop(ItemDrop itemDrop) {
		return itemDropDao.save(itemDrop);
	}

	public List<ItemDrop> getAllItemDrops() {
		return itemDropDao.findAll();
	}

	public List<ItemDrop> getAllReliableItemDrops() {
		return itemDropDao.findAllReliableItemDrops();
	}

	/**
	 * @Title: generateDropMatrixMap
	 * @Description: Use all drop record from item_drop_v2 table, generate a list of sparse matrix elements.<br>
	 * 
	 * @return Map<String,Map<String,DropMatrix>>
	 */
	public List<DropMatrix> generateDropMatrixList() {
		List<DropMatrix> dropMatrixList = new ArrayList<>();
		Map<String, List<Integer>> stageTimesMap = new HashMap<>();
		Map<String, Map<String, DropMatrix>> dropMatrixMap = new HashMap<>();
		List<ItemDrop> itemDrops = itemDropDao.findAllReliableItemDrops();
		logger.debug("size = " + itemDrops.size());
		for (ItemDrop itemDrop : itemDrops) {
			// Calculate how many times each stage has been played under every time point
			List<Integer> subList = stageTimesMap.get(itemDrop.getStageId());
			if (subList == null) {
				subList = new ArrayList<>(ADD_TIME_POINTS.length);
				for (int i = 0; i < ADD_TIME_POINTS.length; i++)
					subList.add(0);
			}
			for (int i = 0; i < ADD_TIME_POINTS.length; i++) {
				if (itemDrop.getTimestamp() >= ADD_TIME_POINTS[i])
					subList.set(i, subList.get(i) + 1);
			}
			stageTimesMap.put(itemDrop.getStageId(), subList);

			// Generate a sub map for one stage, the key is itemId and value is DropMatrix object.
			// Only quantity will be calculated now.
			Map<String, DropMatrix> subMap = dropMatrixMap.getOrDefault(itemDrop.getStageId(), new HashMap<>());
			List<Drop> drops = itemDrop.getDrops();
			for (Drop drop : drops) {
				DropMatrix dropMatrix = subMap.getOrDefault(drop.getItemId(),
						new DropMatrix(itemDrop.getStageId(), drop.getItemId(), 0, 0));
				dropMatrix.increateQuantity(drop.getQuantity());
				subMap.put(drop.getItemId(), dropMatrix);
			}
			dropMatrixMap.put(itemDrop.getStageId(), subMap);
		}
		// Populate 'stage played times' into DropMatrix objects with the help of stageTimesMap.
		Map<String, Item> itemMap = itemService.getItemMap();
		for (String stageId : dropMatrixMap.keySet()) {
			Map<String, DropMatrix> subMap = dropMatrixMap.get(stageId);
			List<Integer> subList = stageTimesMap.get(stageId);
			for (String itemId : subMap.keySet()) {
				DropMatrix dropMatrix = subMap.get(itemId);
				Item item = itemMap.get(itemId);
				Integer addTimePoint = item.getAddTimePoint();
				if (addTimePoint == null)
					addTimePoint = 0;
				dropMatrix.increateTimes(subList.get(addTimePoint));
				dropMatrixList.add(dropMatrix);
				logger.debug(dropMatrix.asJSON().toString());
			}
		}
		return dropMatrixList;
	}

}
