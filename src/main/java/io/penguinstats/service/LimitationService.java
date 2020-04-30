package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.model.Limitation;

public interface LimitationService {

	@Caching(evict = {@CacheEvict(value = "maps", key = "'limitationMap'"),
			@CacheEvict(value = "limitation", key = "#limitation.stageId")})
	void saveLimitation(Limitation limitation);

	List<Limitation> getAllLimitations();

	@Cacheable(value = "limitation", key = "#stageId", unless = "#result == null")
	List<Limitation> getLimitationsByStageId(String stageId);

	@Cacheable(value = "maps", key = "'limitationMap'")
	Map<String, List<Limitation>> getLimitationMap();

	//	@Cacheable(value = "limitation", key = "#stageId", unless = "#result == null")
	//	Limitation getExtendedLimitation(String stageId);

	//	@Cacheable(value = "maps", key = "'extendedLimitationMap'")
	//	Map<String, Limitation> getExtendedLimitationMap();

}
