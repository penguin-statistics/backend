package io.penguinstats.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.LimitationDao;
import io.penguinstats.model.Limitation;

@Service("limitationService")
public class LimitationServiceImpl implements LimitationService {

	@Autowired
	private StageService stageService;

	@Autowired
	private LimitationDao limitationDao;

	@Override
	public void saveLimitation(Limitation limitation) {
		limitationDao.save(limitation);
	}

	/**
	 * @Title: getAllLimitations
	 * @Description: Return all limitations in the database as a list.
	 * @return List<Limitation>
	 */
	@Override
	public List<Limitation> getAllLimitations() {
		return limitationDao.findAll();
	}

	/** 
	 * @Title: getLimitationsByStageId 
	 * @Description: Return all limitations for the indicated stage as a list.
	 * @param stageId
	 * @return List<Limitation>
	 */
	public List<Limitation> getLimitationsByStageId(String stageId) {
		return limitationDao.findLimitationsByStageId(stageId);
	}

	/**
	 * @Title: getLimitationMap
	 * @Description: Return a map which has stageId as key and limitations list as value.
	 * @return Map<String, List<Limitation>>
	 */
	@Override
	public Map<String, List<Limitation>> getLimitationMap() {
		List<Limitation> list = getAllLimitations();
		Map<String, List<Limitation>> map = new HashMap<>();
		list.forEach(limitation -> {

		});
		return map;
	}

}
