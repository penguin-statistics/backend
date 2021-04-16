package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.constant.Constant.CacheValue;
import io.penguinstats.model.Zone;

public interface ZoneService {

    @Caching(evict = {@CacheEvict(value = CacheValue.LISTS, key = "'zoneList'"),
            @CacheEvict(value = CacheValue.MAPS, key = "'zoneMap'")})
    void saveZone(Zone zone);

    Zone getZoneByZoneId(String zoneId);

    Zone getZoneByStageId(String stageId);

    @Cacheable(value = CacheValue.LISTS, key = "'zoneList'")
    List<Zone> getAllZones();

    @Cacheable(value = CacheValue.MAPS, key = "'zoneMap'")
    Map<String, Zone> getZoneMap();

}
