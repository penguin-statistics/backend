package io.penguinstats.controller.v2.api;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("cacheController_v2")
@RequestMapping("/api/v2/cache")
public class CacheController {

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
	@Caching(evict = {@CacheEvict(value = "lists", allEntries = true), @CacheEvict(value = "maps", allEntries = true),
			@CacheEvict(value = "sets", allEntries = true)})
	public ResponseEntity<String> evictStageCache() {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(path = "/time-range")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'timeRangeList'"),
			@CacheEvict(value = "maps", key = "'timeRangeMap'")})
	public ResponseEntity<String> evictTimeRangeCache() {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(path = "/notice")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'noticeList'")})
	public ResponseEntity<String> evictNoticeCache() {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(path = "/property")
	@Caching(evict = {@CacheEvict(value = "maps", key = "'propertiesMap'")})
	public ResponseEntity<String> evictSystemPropertiesCache() {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
