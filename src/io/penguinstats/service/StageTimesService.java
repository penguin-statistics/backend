package io.penguinstats.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import io.penguinstats.bean.StageTimes;
import io.penguinstats.dao.StageTimesDao;
import io.penguinstats.util.Tuple;

public class StageTimesService {

	private static StageTimesService instance = new StageTimesService();
	private static StageTimesDao dao = new StageTimesDao();

	private StageTimesService() {}

	public static StageTimesService getInstance() {
		return instance;
	}

	public boolean saveStageTimes(StageTimes stageTimes) {
		return dao.save(stageTimes);
	}

	public int getTimesForOneStage(int stageID, String stageType) {
		StageTimes st = dao.findByStageIDAndStageType(stageID, stageType);
		return st == null ? -1 : st.getTimes();
	}

	public Map<Tuple<Integer, String>, Integer> getStageTimesMap() {
		Map<Tuple<Integer, String>, Integer> map = new HashMap<>();
		List<StageTimes> list = dao.findAll();
		for (StageTimes st : list) {
			map.put(new Tuple<>(st.getStageID(), st.getStageType()), st.getTimes());
		}
		return map;
	}

	public void addStageTimes(int stageID, String stageType, int times) {
		StageTimes st = dao.findByStageIDAndStageType(stageID, stageType);
		if (st == null) {
			dao.save(new StageTimes(stageID, stageType, times));
		} else {
			Document doc = dao.findDocumentByStageIDAndStageType(stageID, stageType);
			doc.put("times", st.getTimes() + times);
			dao.updateDocument(doc);
		}
	}

	public boolean clearAndUpdateAll(Map<Tuple<Integer, String>, Integer> map) {
		boolean deleteResult = dao.batchDelete();
		if (!deleteResult)
			return false;
		List<StageTimes> list = new ArrayList<>();
		for (Tuple<Integer, String> tuple : map.keySet()) {
			int stageID = tuple.getX();
			String stageType = tuple.getY();
			Integer times = map.get(tuple);
			StageTimes st = new StageTimes(stageID, stageType, times);
			list.add(st);
		}
		dao.batchSave(list);
		return true;
	}

}
