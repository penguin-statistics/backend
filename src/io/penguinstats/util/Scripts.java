package io.penguinstats.util;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import io.penguinstats.bean.Drop;
import io.penguinstats.bean.DropMatrix;
import io.penguinstats.bean.ItemDrop;
import io.penguinstats.dao.BaseDao;
import io.penguinstats.dao.DropMatrixDao;
import io.penguinstats.dao.ItemDropDao;
import io.penguinstats.service.ItemDropService;

public class Scripts {

	private static final ItemDropDao itemDropDao = new ItemDropDao();
	private static final DropMatrixDao dropMatrixDao = new DropMatrixDao();
	private static final ItemDropService itemDropService = ItemDropService.getInstance();

	public static void main(String[] args) {
		List<DropMatrix> dropMatrixMap = itemDropService.generateDropMatrixList();
		dropMatrixDao.batchSave(dropMatrixMap);
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
				System.out.println(itemDrop.asJSON().toString(2));
			}
		}
		if (itemDropDao.batchDelete()) { // WARNING: Check table name!
			itemDropDao.batchSave(itemDrops);
		}
	}

	@SuppressWarnings("rawtypes")
	private static class Dao extends BaseDao {

		public Dao(String collectionName) {
			super(collectionName);
		}

	}

}
