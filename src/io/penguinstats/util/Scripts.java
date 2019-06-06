package io.penguinstats.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import io.penguinstats.bean.Drop;
import io.penguinstats.bean.DropMatrix;
import io.penguinstats.bean.ItemDrop;
import io.penguinstats.dao.BaseDao;
import io.penguinstats.dao.ItemDropDao;
import io.penguinstats.service.DropMatrixService;
import io.penguinstats.service.ItemDropService;

public class Scripts {

	private static final ItemDropDao itemDropDao = new ItemDropDao();
	private static final DropMatrixService dropMatrixService = DropMatrixService.getInstance();
	private static final ItemDropService itemDropService = ItemDropService.getInstance();

	public static void main(String[] args) {
		outputAllReliableItemDrops();
	}

	@SuppressWarnings("unchecked")
	private static void convertOldItemDrop() {
		Dao oldDao = new Dao("item_drop");
		List<Document> documents = oldDao.findAllDocuments();
		List<ItemDrop> itemDrops = new ArrayList<>();
		for (Document document : documents) {
			String stageId = ConvertHelper.stageIdMap.get(document.getInteger("stageID"));
			Integer times = document.getInteger("times");
			Long timestamp = document.getLong("timestamp");
			String ip = document.getString("ip");
			Boolean isReliable = document.getBoolean("isAbnormal") == null ? true : !document.getBoolean("isAbnormal");
			List<Document> dropsDocList = (ArrayList<Document>)document.get("drops");
			List<Drop> drops = new ArrayList<>();
			dropsDocList.forEach(dropDoc -> {
				String itemId = ConvertHelper.itemIdMap.get(dropDoc.getInteger("itemID"));
				if (itemId != null) {
					drops.add(new Drop(itemId, dropDoc.getInteger("quantity")));
				}
			});
			Integer furnitureNum = document.getInteger("furnitureNum");
			if (furnitureNum != null && furnitureNum > 0) {
				drops.add(new Drop("furni", furnitureNum));
			}
			if (stageId != null && times != null && timestamp != null && ip != null) {
				ItemDrop itemDrop = new ItemDrop(stageId, times, drops, timestamp, ip, isReliable);
				itemDrops.add(itemDrop);
				System.out.println(itemDrop.asJSON().toString());
			}
		}
		if (itemDropDao.batchDelete()) { // WARNING: Check table name!
			itemDropDao.batchSave(itemDrops);
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

	@SuppressWarnings("rawtypes")
	private static class Dao extends BaseDao {

		public Dao(String collectionName) {
			super(collectionName);
		}

	}

}
