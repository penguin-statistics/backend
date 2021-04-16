package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.constant.Constant.CacheValue;
import io.penguinstats.model.Stage;

public interface StageService {

    @Caching(evict = {@CacheEvict(value = CacheValue.LISTS, key = "'stageList'"),
            @CacheEvict(value = CacheValue.MAPS, key = "'stageMap'")})
    void saveStage(Stage stage);

    Stage getStageByStageId(String stageId);

    List<Stage> getStagesByZoneId(String zoneId);

    @Cacheable(value = CacheValue.LISTS, key = "'stageList'")
    List<Stage> getAllStages();

    @Cacheable(value = CacheValue.MAPS, key = "'stageMap'")
    Map<String, Stage> getStageMap();

    @Cacheable(value = CacheValue.MAPS, key = "'allCodeStageMap'")
    Map<String, Stage> getAllCodeStageMap();

}
