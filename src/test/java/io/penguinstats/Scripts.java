package io.penguinstats;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.penguinstats.model.Drop;
import io.penguinstats.model.DropMatrix;
import io.penguinstats.model.Item;
import io.penguinstats.model.ItemDrop;
import io.penguinstats.model.Limitation;
import io.penguinstats.model.Stage;
import io.penguinstats.service.DropMatrixService;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.ItemService;
import io.penguinstats.service.LimitationService;
import io.penguinstats.service.StageService;
import io.penguinstats.service.UserService;
import io.penguinstats.service.ZoneService;
import io.penguinstats.util.LimitationUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@SuppressWarnings("unused")
public class Scripts {

	@Test
	public void executeScripts() throws JsonProcessingException {
		//		printAllItemDropDistribution();
		//		printAllStageLimitations();
		//		checkAllItemDrops();
		//		clearAndUpdateDropMatrix();
		// checkSubmitTime();
	}

	//	public static class IPTime {
	//		String ip;
	//		int submitTimes;
	//		int fakeTimes;
	//		Long avg;
	//
	//		public IPTime(String ip, int submitTimes, int fakeTimes, Long avg) {
	//			this.ip = ip;
	//			this.submitTimes = submitTimes;
	//			this.fakeTimes = fakeTimes;
	//			this.avg = avg;
	//		}
	//
	//		public IPTime increaseFakeTime() {
	//			this.fakeTimes++;
	//			return this;
	//		}
	//	}
	//
	//		private static void checkSubmitTime() {
	//			ItemDropDao dao = new ItemDropDao();
	//			List<Document> docList = dao.findAllDocuments();
	//			Map<String, List<Document>> map = new HashMap<>();
	//			Map<String, IPTime> ipMap = new HashMap<>();
	//			Map<String, Integer> stageNumMap = new HashMap<>();
	//			for (Document doc : docList) {
	//				if (doc.getBoolean("isReliable") == null || !doc.getBoolean("isReliable") || doc.getString("ip") == null)
	//					continue;
	//				if (!map.containsKey(doc.getString("ip"))) {
	//					map.put(doc.getString("ip"), new ArrayList<>());
	//				}
	//				List<Document> docs = map.get(doc.getString("ip"));
	//				docs.add(doc);
	//			}
	//			Long THRESHOLD = 2000L;
	//			int stat1 = 0;
	//			int stat2 = 0;
	//			int stat3 = 0;
	//			int stat4 = 0;
	//			for (String ip : map.keySet()) {
	//				List<Document> docs = map.get(ip);
	//	
	//				if (docs.size() == 1) {
	//					stat1++;
	//				} else if (docs.size() == 2) {
	//					stat2++;
	//				} else if (docs.size() == 3) {
	//					stat3++;
	//				} else {
	//					stat4++;
	//				}
	//	
	//				Long sum = 0L;
	//				for (int i = 0; i < docs.size() - 1; i++) {
	//					Document docOne = docs.get(i);
	//					Document docTwo = docs.get(i + 1);
	//					Long distance = docTwo.getLong("timestamp") - docOne.getLong("timestamp");
	//					sum += distance;
	//					if (distance < THRESHOLD) {
	//						ipMap.put(ip, ipMap.getOrDefault(ip, new IPTime(ip, docs.size(), 0, 0L)).increaseFakeTime());
	//					}
	//				}
	//				Long avg = 0L;
	//				if (docs.size() > 1) {
	//					avg = sum / docs.size() - 1;
	//				}
	//				if (ipMap.containsKey(ip)) {
	//					ipMap.get(ip).avg = avg;
	//				}
	//			}
	//			System.out.println("one: " + stat1);
	//			System.out.println("one: " + stat2);
	//			System.out.println("one: " + stat3);
	//			System.out.println("one: " + stat4);
	//			System.out.println("ips = " + ipMap.keySet().size());
	//			List<IPTime> ipTimes = new ArrayList<>();
	//			for (String ip : ipMap.keySet()) {
	//				ipTimes.add(ipMap.get(ip));
	//			}
	//			ipTimes.sort((a, b) -> b.fakeTimes - a.fakeTimes);
	//			int submitTotal = 0;
	//			Set<String> set = new HashSet<>();
	//			for (IPTime ipTime : ipTimes) {
	//				if (ipTime.avg < 3000L || (ipTime.fakeTimes * 1.0 / ipTime.submitTimes) >= 0.5) {
	//					System.out.println(ipTime.ip + " " + ipTime.fakeTimes + "/" + ipTime.submitTimes + " avg = "
	//							+ (ipTime.avg * 1.0 / 1000) + "s");
	//					submitTotal += ipTime.submitTimes;
	//					set.add(ipTime.ip);
	//				}
	//			}
	//			System.out.println(submitTotal);
	//			int sum = 0;
	//			for (Document doc : docList) {
	//				if (doc.getBoolean("isReliable") == null || !doc.getBoolean("isReliable") || doc.getString("ip") == null)
	//					continue;
	//				if (set.contains(doc.getString("ip"))) {
	//					sum++;
	//					stageNumMap.put(doc.getString("stageId"), stageNumMap.getOrDefault(doc.getString("stageId"), 0) + 1);
	//					// doc.put("isReliable", false);
	//					// dao.updateDocument(doc);
	//					// System.out.println(sum + " " + doc.toJson().toString());
	//				}
	//			}
	//			System.out.println(stageNumMap);
	//			System.out.println(sum);
	//		}
	//	
	public static class IPBan {
		String ip;
		int banNum;

		IPBan(String ip, int banNum) {
			this.ip = ip;
			this.banNum = banNum;
		}

		public IPBan increase() {
			this.banNum++;
			return this;
		}
	}

	private void checkAllItemDrops() {
		List<ItemDrop> list = itemDropService.getAllReliableItemDrops();
		Map<String, List<ItemDrop>> map = new HashMap<>();
		Map<String, Limitation> limitationMap = limitationService.getLimitationMap();
		Map<String, Item> itemMap = itemService.getItemMap();
		Map<String, IPBan> ipMap = new HashMap<>();
		List<ItemDrop> toUpdate = new ArrayList<>();
		for (ItemDrop itemDrop : list) {
			List<Drop> drops = itemDrop.getDrops();
			if (drops != null) {
				if (!limitationUtil.checkDrops(drops, itemDrop.getStageId(), itemDrop.getTimestamp(), limitationMap,
						itemMap)) {
					List<ItemDrop> itemDrops = map.getOrDefault(itemDrop.getStageId(), new ArrayList<>());
					itemDrops.add(itemDrop);
					map.put(itemDrop.getStageId(), itemDrops);
					ipMap.put(itemDrop.getIp(),
							ipMap.getOrDefault(itemDrop.getIp(), new IPBan(itemDrop.getIp(), 0)).increase());

					itemDrop.setIsReliable(false);
					toUpdate.add(itemDrop);
				}
			}
		}

		System.out.println("size = " + toUpdate.size());
		itemDropService.batchSaveItemDrops(toUpdate);

		int sum = 0;
		for (String stageId : map.keySet()) {
			System.out.println(stageId + ": " + map.get(stageId).size());
			sum += map.get(stageId).size();
		}
		System.out.println("sum = " + sum);
		System.out.println("ips = " + ipMap.keySet().size());
		List<IPBan> ipBans = new ArrayList<>();
		for (String ip : ipMap.keySet()) {
			ipBans.add(ipMap.get(ip));
		}
		ipBans.sort((a, b) -> b.banNum - a.banNum);
		for (IPBan ipBan : ipBans) {
			System.out.println(ipBan.ip + " " + ipBan.banNum);
		}
	}

	private void printAllStageLimitations() {
		Map<String, Limitation> map = limitationService.getRealLimitationMap();
		for (String stageId : map.keySet())
			System.out.println(map.get(stageId).toString());
	}

	private void printAllItemDropDistribution() {
		List<ItemDrop> list = itemDropService.getAllReliableItemDrops();
		System.out.println("Find all itemDrops: size = " + list.size());
		Map<String, List<ItemDrop>> mapByStage = new TreeMap<>(new Comparator<String>() {
			public int compare(String s1, String s2) {
				return s1.compareTo(s2);
			}
		});
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
			// if (drops.size() < 200)
			// continue;
			Map<String, Map<Integer, Integer>> itemDropNumMap = new HashMap<>();
			Map<Integer, Integer> subDropTypesMap = new HashMap<>();
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
				subDropTypesMap.put(0, subDropTypesMap.getOrDefault(0, drops.size()) - 1);
				subDropTypesMap.put(dropTypes, subDropTypesMap.getOrDefault(dropTypes, 0) + 1);
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
			Set<String> dropSet = stage.getDropsSet();
			for (String itemId : itemDropNumMap.keySet()) {
				System.out.println("\t素材：" + itemId + " " + itemDropNumMap.get(itemId).toString());
				dropSet.remove(itemId);
			}
			if (!dropSet.isEmpty())
				System.out.println("\t无掉落汇报：" + dropSet.toString());
			System.out.println("掉落种类数分布：" + subDropTypesMap.toString());
			System.out.println("-----------------------------------------\n");
		}
		System.out.println("全体掉落种类数分布：");
		System.out.println(dropTypesMap.toString());
	}
	//
	//	private static void printDrops(String stageId, String itemId, int hoursPerWindow) {
	//		class MyClass {
	//			int times;
	//			int quantity;
	//
	//			MyClass() {
	//				this.times = 0;
	//				this.quantity = 0;
	//			}
	//		}
	//		ItemDropDao dao = new ItemDropDao();
	//		List<ItemDrop> list = dao.findAllReliableItemDropsByStageId(stageId);
	//		System.out.println(list.size());
	//		Long window = hoursPerWindow * 60 * 60 * 1000L;
	//		Long startTime = 1558092121113L;
	//		int windowNum = new Long((System.currentTimeMillis() - startTime) / window + 1).intValue();
	//		MyClass[] array = new MyClass[windowNum];
	//		for (int i = 0; i < windowNum; i++) {
	//			array[i] = new MyClass();
	//		}
	//		for (ItemDrop itemDrop : list) {
	//			Long dist = itemDrop.getTimestamp() - startTime;
	//			int index = new Long(dist / window).intValue();
	//			array[index].times += itemDrop.getTimes();
	//			array[index].quantity += itemDrop.getDropQuantity(itemId);
	//		}
	//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
	//		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
	//		for (int i = 0; i < windowNum; i++) {
	//			Long time = startTime + i * window;
	//			System.out.println(sdf.format(new Date(time)) + " 样本数：" + array[i].times + " 掉落数：" + array[i].quantity
	//					+ " 掉落率：" + String.format("%.2f", 100.0 * array[i].quantity / array[i].times) + "%");
	//		}
	//	}

	private void clearAndUpdateDropMatrix() throws JsonProcessingException {
		List<DropMatrix> elements = itemDropService.generateDropMatrixList(new Criteria()
				.andOperator(Criteria.where("isReliable").is(true), Criteria.where("isDeleted").is(false)));
		dropMatrixService.clearAndUpdateAll(elements);
	}

	@Autowired
	private ZoneService zoneService;
	@Autowired
	private StageService stageService;
	@Autowired
	private UserService userService;
	@Autowired
	private LimitationService limitationService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemDropService itemDropService;
	@Autowired
	private DropMatrixService dropMatrixService;
	@Autowired
	private LimitationUtil limitationUtil;

	private ObjectMapper mapper = new ObjectMapper();

}
