package io.penguinstats.service;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.LimitationDao;
import io.penguinstats.model.Limitation;
import io.penguinstats.model.Stage;
import io.penguinstats.util.LastUpdateTimeUtil;

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
	 * @Title: getLimitationMap
	 * @Description: Return a map which has stageId (usually will be limitation name, sometimes will be user-defined
	 *               name) as key and limitation object as value.
	 * @return Map<String,Limitation>
	 */
	@Override
	public Map<String, Limitation> getLimitationMap() {
		List<Limitation> list = getAllLimitations();
		Map<String, Limitation> map = new HashMap<>();
		list.forEach(limitation -> map.put(limitation.getName(), limitation));
		return map;
	}

	/**
	 * @Title: getExtendedLimitation
	 * @Description: Return the limitation of given stage. The inheritance will be iterated using DFS and merged into
	 *               limitations.
	 * @param stageId
	 * @return Limitation
	 */
	@Override
	public Limitation getExtendedLimitation(String stageId) {
		Map<String, Limitation> limitationMap = getLimitationMap();
		Map<String, Stage> stageMap = stageService.getStageMap();
		if (!stageMap.containsKey(stageId))
			return limitationMap.get("all");
		return iterateInheritance(stageId, limitationMap, stageMap);
	}

	/**
	 * @Title: getExtendedLimitationMap
	 * @Description: Return a map which has stageId (will never be limitation name, only can be stageId) as key and
	 *               limitation object as value. The inheritance will be iterated and merged into limitations.
	 * @return Map<String,Limitation>
	 */
	@Override
	public Map<String, Limitation> getExtendedLimitationMap() {
		Map<String, Limitation> limitationMap = getLimitationMap();
		Map<String, Stage> stageMap = stageService.getStageMap();
		Map<String, Limitation> result = new HashMap<>();
		for (String stageId : stageMap.keySet()) {
			Limitation limitation = iterateInheritance(stageId, limitationMap, stageMap);
			result.put(stageId, limitation);
		}
		LastUpdateTimeUtil.setCurrentTimestamp("extendedLimitationMap");
		return result;
	}

	/**
	 * @Title: iterateInheritance
	 * @Description: A helper function to iterate all inheritance and merge their limitations into the original one
	 *               using DFS.
	 * @param stageId
	 * @param limitationMap
	 * @param stageMap
	 * @return Limitation
	 */
	private Limitation iterateInheritance(String stageId, Map<String, Limitation> limitationMap,
			Map<String, Stage> stageMap) {
		Deque<String> stack = new LinkedList<>();
		Set<String> hasIterated = new HashSet<>();
		stack.offerFirst(stageId);
		Limitation limitation = new Limitation(stageId, null, new ArrayList<>(), new ArrayList<>());
		while (!stack.isEmpty()) {
			String oneStageId = stack.pollFirst();
			if (hasIterated.contains(oneStageId))
				continue;
			else
				hasIterated.add(oneStageId);
			Limitation oneLimitation = limitationMap.get(oneStageId);
			if (oneLimitation == null)
				continue;
			limitation.merge(oneLimitation);
			int size = oneLimitation.getInheritance().size();
			for (int i = size - 1; i >= 0; i--) {
				stack.offerFirst(oneLimitation.getInheritance().get(i));
			}
		}
		limitation.merge(limitationMap.get("all"));
		limitation.filterItemQuantityBounds(stageMap.get(stageId).getDropsSet());
		return limitation;
	}

}
