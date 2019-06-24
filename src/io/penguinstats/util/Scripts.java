package io.penguinstats.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import io.penguinstats.bean.Drop;
import io.penguinstats.bean.DropMatrix;
import io.penguinstats.bean.ItemDrop;
import io.penguinstats.bean.Stage;
import io.penguinstats.dao.ItemDropDao;
import io.penguinstats.service.DropMatrixService;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.StageService;

public class Scripts {

	private static final DropMatrixService dropMatrixService = DropMatrixService.getInstance();
	private static final ItemDropService itemDropService = ItemDropService.getInstance();
	private static final StageService stageService = StageService.getInstance();

	public static void main(String[] args) {
		printAllItemDropDistribution();
	}

	private static void printAllItemDropDistribution() {
		ItemDropDao dao = new ItemDropDao();
		List<ItemDrop> list = dao.findAllReliableItemDrops();
		System.out.println("Find all itemDrops: size = " + list.size());
		Map<String, List<ItemDrop>> mapByStage = new HashMap<>();
		Map<Integer, Integer> dropTypesMap = new HashMap<>();
		for (ItemDrop itemDrop : list) {
			String stageId = itemDrop.getStageId();
			if (!mapByStage.containsKey(stageId)) {
				mapByStage.put(stageId, new ArrayList<>());
			}
			mapByStage.get(stageId).add(itemDrop);
		}
		Map<String, Stage> stageMap = stageService.getStageMap();
		for (String stageId : mapByStage.keySet()) {
			List<ItemDrop> drops = mapByStage.get(stageId);
			if (drops.size() < 200)
				continue;
			Map<String, Map<Integer, Integer>> itemDropNumMap = new HashMap<>();
			for (ItemDrop itemDrop : drops) {
				List<Drop> ds = itemDrop.getDrops();
				boolean containsFurniture = false;
				for (Drop drop : ds) {
					if (drop.getItemId().equals("furni"))
						containsFurniture = true;
					String itemId = drop.getItemId();
					itemDropNumMap.putIfAbsent(itemId, new HashMap<>());
					Map<Integer, Integer> dropNumsMap = itemDropNumMap.get(itemId);
					int quantity = drop.getQuantity();
					dropNumsMap.put(0, dropNumsMap.getOrDefault(0, drops.size()) - 1);
					dropNumsMap.put(quantity, dropNumsMap.getOrDefault(quantity, 0) + 1);
				}
				int dropTypes = ds.size();
				if (containsFurniture)
					dropTypes = Math.max(0, dropTypes - 1);
				dropTypesMap.put(0, dropTypesMap.getOrDefault(0, list.size()) - 1);
				dropTypesMap.put(dropTypes, dropTypesMap.getOrDefault(dropTypes, 0) + 1);
			}

			System.out.println("-----------------------------------------");
			System.out.println(stageId + " 样本数：" + drops.size());
			Stage stage = stageMap.get(stageId);
			if (!stage.getNormalDrop().isEmpty())
				System.out.println("普通掉落：" + stage.getNormalDrop());
			if (!stage.getSpecialDrop().isEmpty())
				System.out.println("特殊掉落：" + stage.getSpecialDrop());
			if (!stage.getExtraDrop().isEmpty())
				System.out.println("额外掉落：" + stage.getExtraDrop());
			Set<String> dropSet = new HashSet<>();
			dropSet.addAll(stage.getNormalDrop());
			dropSet.addAll(stage.getSpecialDrop());
			dropSet.addAll(stage.getExtraDrop());
			for (String itemId : itemDropNumMap.keySet()) {
				System.out.println("\t素材：" + itemId + " " + itemDropNumMap.get(itemId).toString());
				dropSet.remove(itemId);
			}
			if (!dropSet.isEmpty())
				System.out.println("\t无掉落汇报：" + dropSet.toString());
			System.out.println("-----------------------------------------\n");
		}
		System.out.println("全体掉落数量分布：");
		System.out.println(dropTypesMap.toString());
	}

	private static void printDrops(String stageId, String itemId, int hoursPerWindow) {
		class MyClass {
			int times;
			int quantity;

			MyClass() {
				this.times = 0;
				this.quantity = 0;
			}
		}
		ItemDropDao dao = new ItemDropDao();
		List<ItemDrop> list = dao.findAllReliableItemDropsByStageId(stageId);
		System.out.println(list.size());
		Long window = hoursPerWindow * 60 * 60 * 1000L;
		Long startTime = 1558092121113L;
		int windowNum = new Long((System.currentTimeMillis() - startTime) / window + 1).intValue();
		MyClass[] array = new MyClass[windowNum];
		for (int i = 0; i < windowNum; i++) {
			array[i] = new MyClass();
		}
		for (ItemDrop itemDrop : list) {
			Long dist = itemDrop.getTimestamp() - startTime;
			int index = new Long(dist / window).intValue();
			array[index].times += itemDrop.getTimes();
			array[index].quantity += itemDrop.getDropQuantity(itemId);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		for (int i = 0; i < windowNum; i++) {
			Long time = startTime + i * window;
			System.out.println(sdf.format(new Date(time)) + " 样本数：" + array[i].times + " 掉落数：" + array[i].quantity
					+ " 掉落率：" + String.format("%.2f", 100.0 * array[i].quantity / array[i].times) + "%");
		}
	}

	private static void clearAndUpdateDropMatrix() {
		List<DropMatrix> elements = itemDropService.generateDropMatrixList();
		dropMatrixService.clearAndUpdateAll(elements);
	}

	private static void outputAllReliableItemDrops() {
		List<ItemDrop> itemDrops = itemDropService.getAllReliableItemDrops();
		JSONArray array = new JSONArray();
		for (ItemDrop itemDrop : itemDrops) {
			array.put(itemDrop.asJSON());
		}
		JSONObject obj = new JSONObject().put("itemDrops", array);
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter("itemDrops.json"));
			writer.write(obj.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
