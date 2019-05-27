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

	public List<Integer> getTimesForOneStage(int stageID, String stageType) {
		StageTimes st = dao.findByStageIDAndStageType(stageID, stageType);
		return st == null ? new ArrayList<>() : st.getTimes();
	}

	public Map<Tuple<Integer, String>, Map<Integer, Integer>> getStageTimesMap() {
		Map<Tuple<Integer, String>, Map<Integer, Integer>> map = new HashMap<>();
		List<StageTimes> list = dao.findAll();
		for (StageTimes st : list) {
			Tuple<Integer, String> tuple = new Tuple<>(st.getStageID(), st.getStageType());
			Map<Integer, Integer> subMap = new HashMap<>();
			List<Integer> times = st.getTimes();
			for (int i = 0; i < times.size(); i++) {
				subMap.put(i, times.get(i));
			}
			map.put(tuple, subMap);
		}
		return map;
	}

	public void addStageTimes(int stageID, String stageType, int times, Long timestamp) {
		StageTimes st = dao.findByStageIDAndStageType(stageID, stageType);
		if (st == null) {
			List<Integer> timesList = new ArrayList<>();
			for (int i = 0; i < ItemDropService.TIME_POINTS.length; i++) {
				if (timestamp >= ItemDropService.TIME_POINTS[i])
					timesList.add(times);
				else
					timesList.add(0);
			}
			dao.save(new StageTimes(stageID, stageType, timesList));
		} else {
			Document doc = dao.findDocumentByStageIDAndStageType(stageID, stageType);
			List<Integer> oldTimes = st.getTimes();
			for (int i = 0; i < oldTimes.size(); i++) {
				if (timestamp >= ItemDropService.TIME_POINTS[i])
					oldTimes.set(i, oldTimes.get(i) + times);
			}
			doc.put("times", oldTimes);
			dao.updateDocument(doc);
		}
	}

	public boolean clearAndUpdateAll(Map<Tuple<Integer, String>, Map<Integer, Integer>> map) {
		boolean deleteResult = dao.batchDelete();
		if (!deleteResult)
			return false;
		List<StageTimes> list = new ArrayList<>();
		for (Tuple<Integer, String> tuple : map.keySet()) {
			int stageID = tuple.getX();
			String stageType = tuple.getY();
			Map<Integer, Integer> timesMap = map.get(tuple);
			List<Integer> times = new ArrayList<>();
			for (Integer key : timesMap.keySet()) {
				times.add(timesMap.get(key));
			}
			StageTimes st = new StageTimes(stageID, stageType, times);
			list.add(st);
		}
		dao.batchSave(list);
		return true;
	}

}
