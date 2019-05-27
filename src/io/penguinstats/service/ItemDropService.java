package io.penguinstats.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.penguinstats.bean.Drop;
import io.penguinstats.bean.ItemDrop;
import io.penguinstats.dao.ItemDropDao;
import io.penguinstats.util.Tuple;

public class ItemDropService {

	private static ItemDropService instance = new ItemDropService();
	private static ItemDropDao dao = new ItemDropDao();

	public static final Long[] TIME_POINTS = new Long[] {0L, 1558948717000L};

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

	public Map<Tuple<Integer, String>, Map<Integer, Integer>> generateStageTimesMap() {
		Map<Tuple<Integer, String>, Map<Integer, Integer>> result = new HashMap<>();
		List<ItemDrop> list = dao.findAll();
		for (ItemDrop itemDrop : list) {
			Boolean isAbnormal = itemDrop.getIsAbnormal();
			if (isAbnormal == null || !isAbnormal) {
				int stageID = itemDrop.getStageID();
				String stageType = itemDrop.getStageType();
				Tuple<Integer, String> tuple = new Tuple<>(stageID, stageType);
				Map<Integer, Integer> subMap = result.getOrDefault(tuple, new HashMap<>());
				for (int i = 0; i < TIME_POINTS.length; i++) {
					if (!subMap.containsKey(i)) {
						subMap.put(i, 0);
					}
					if (itemDrop.getTimestamp() >= TIME_POINTS[i]) {
						subMap.put(i, subMap.get(i) + itemDrop.getTimes());
					}
				}
				result.put(tuple, subMap);
			}
		}
		return result;
	}

	public Map<Tuple<Integer, String>, Map<Integer, Integer>> generateDropMatrixMap() {
		Map<Tuple<Integer, String>, Map<Integer, Integer>> result = new HashMap<>();
		List<ItemDrop> list = dao.findAll();
		for (ItemDrop itemDrop : list) {
			Boolean isAbnormal = itemDrop.getIsAbnormal();
			if (isAbnormal == null || !isAbnormal) {
				Tuple<Integer, String> stageTuple = new Tuple<>(itemDrop.getStageID(), itemDrop.getStageType());
				Map<Integer, Integer> subMap = result.getOrDefault(stageTuple, new HashMap<>());
				List<Drop> drops = itemDrop.getDrops();
				for (Drop drop : drops) {
					subMap.put(drop.getItemID(), subMap.getOrDefault(drop.getItemID(), 0) + drop.getQuantity());
				}
				if (itemDrop.getFurnitureNum() != 0) {
					subMap.put(-1, subMap.getOrDefault(-1, 0) + itemDrop.getFurnitureNum());
				}
				result.put(stageTuple, subMap);
			}
		}
		return result;
	}

	public static void main(String[] args) {
		ItemDropService service = ItemDropService.getInstance();
		Map<Tuple<Integer, String>, Map<Integer, Integer>> stageTimesMap = service.generateStageTimesMap();
		Map<Tuple<Integer, String>, Map<Integer, Integer>> dropMatrixMap = service.generateDropMatrixMap();
		StageTimesService.getInstance().clearAndUpdateAll(stageTimesMap);
		DropMatrixService.getInstance().clearAndUpdateAll(dropMatrixMap);
		// System.out.println(stageTimesMap);
	}

}
