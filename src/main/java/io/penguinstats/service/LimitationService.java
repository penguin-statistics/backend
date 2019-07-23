package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import io.penguinstats.model.Limitation;

public interface LimitationService {

	void saveLimitation(Limitation limitation);

	List<Limitation> getAllLimitations();

	Map<String, Limitation> getLimitationMap();

	Limitation getRealLimitation(String stageId);

	Map<String, Limitation> getRealLimitationMap();

}
