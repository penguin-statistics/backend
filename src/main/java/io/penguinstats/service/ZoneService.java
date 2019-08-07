package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.model.Zone;

public interface ZoneService {

	@Caching(evict = {@CacheEvict(value = "lists", key = "'zoneList'"), @CacheEvict(value = "maps", key = "'zoneMap'")})
	void saveZone(Zone zone);

	Zone getZoneByZoneId(String zoneId);

	@Cacheable(value = "lists", key = "'zoneList'")
	List<Zone> getAllZones();

	@Cacheable(value = "maps", key = "'zoneMap'")
	Map<String, Zone> getZoneMap();

}
