package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.model.Stage;

public interface StageService {

	@Caching(evict = {@CacheEvict(value = "lists", key = "'stageList'"),
			@CacheEvict(value = "maps", key = "'stageMap'")})
	void saveStage(Stage stage);

	Stage getStageByStageId(String stageId);

	List<Stage> getStagesByZoneId(String zoneId);

	@Cacheable(value = "lists", key = "'stageList'")
	List<Stage> getAllStages();

	@Cacheable(value = "maps", key = "'stageMap'")
	Map<String, Stage> getStageMap();

}
