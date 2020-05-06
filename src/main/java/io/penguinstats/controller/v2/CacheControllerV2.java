package io.penguinstats.controller.v2;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/cache")
public class CacheControllerV2 {

	@DeleteMapping(path = "/item")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'itemList'"), @CacheEvict(value = "maps", key = "'itemMap'")})
	public ResponseEntity<String> evictItemCache() {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(path = "/zone")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'zoneList'"), @CacheEvict(value = "maps", key = "'zoneMap'")})
	public ResponseEntity<String> evictZoneCache() {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(path = "/stage")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'stageList'"),
			@CacheEvict(value = "maps", key = "'stageMap'"), @CacheEvict(value = "drop-info-list", allEntries = true),
			@CacheEvict(value = "latest-time-range-map", allEntries = true),
			@CacheEvict(value = "dropset", allEntries = true)})
	public ResponseEntity<String> evictStageCache() {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
