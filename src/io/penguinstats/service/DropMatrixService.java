package io.penguinstats.service;

import io.penguinstats.bean.DropMatrix;
import io.penguinstats.dao.DropMatrixDao;

public class DropMatrixService {

	private static DropMatrixService instance = new DropMatrixService();
	private static DropMatrixDao dao = new DropMatrixDao();

	private DropMatrixService() {}

	public static DropMatrixService getInstance() {
		return instance;
	}

	public boolean saveDropMatrix(DropMatrix dropMatrix) {
		return dao.save(dropMatrix);
	}

	// public Map<Tuple<Integer, String>, Map<Integer, Integer>> getDropMatrixMap() {
	// Map<Tuple<Integer, String>, Map<Integer, Integer>> map = new HashMap<>();
	// List<DropMatrix> list = dao.findAll();
	// for (DropMatrix dm : list) {
	// Tuple<Integer, String> tuple = new Tuple<>(dm.getStageID(), dm.getStageType());
	// Map<Integer, Integer> subMap = map.getOrDefault(tuple, new HashMap<>());
	// subMap.put(dm.getItemID(), subMap.getOrDefault(dm.getItemID(), 0) + dm.getQuantity());
	// map.put(tuple, subMap);
	// }
	// return map;
	// }
	//
	// public void addDropMatrix(int stageID, String stageType, int itemID, int quantity, int times) {
	// DropMatrix dm = dao.findByStageIDAndStageTypeAndItemID(stageID, stageType, itemID);
	// if (dm == null) {
	// dao.save(new DropMatrix(stageID, stageType, itemID, quantity, times));
	// } else {
	// Document doc = dao.findDocumentByStageIDAndStageTypeAndItemID(stageID, stageType, itemID);
	// doc.put("quantity", dm.getQuantity() + quantity);
	// dao.updateDocument(doc);
	// }
	// }
	//
	// public boolean clearAndUpdateAll(Map<Tuple<Integer, String>, Map<Integer, Integer>> map) {
	// boolean deleteResult = dao.batchDelete();
	// if (!deleteResult)
	// return false;
	// List<DropMatrix> list = new ArrayList<>();
	// for (Tuple<Integer, String> tuple : map.keySet()) {
	// int stageID = tuple.getX();
	// String stageType = tuple.getY();
	// Map<Integer, Integer> subMap = map.get(tuple);
	// for (Integer itemID : subMap.keySet()) {
	// int quantity = subMap.get(itemID);
	// DropMatrix dm = new DropMatrix(stageID, stageType, itemID, quantity);
	// list.add(dm);
	// }
	// }
	// dao.batchSave(list);
	// return true;
	// }

}
