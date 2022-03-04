package io.penguinstats.controller.v2.api;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant.CacheValue;

@RestController("cacheController_v2")
@RequestMapping("/api/v2/cache")
public class CacheController {

    @DeleteMapping(path = "/item")
    @Caching(evict = {@CacheEvict(value = CacheValue.LISTS, key = "'itemList'"),
            @CacheEvict(value = CacheValue.MAPS, key = "'itemMap'")})
    public ResponseEntity<String> evictItemCache() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/zone")
    @Caching(evict = {@CacheEvict(value = CacheValue.LISTS, key = "'zoneList'"),
            @CacheEvict(value = CacheValue.MAPS, key = "'zoneMap'")})
    public ResponseEntity<String> evictZoneCache() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/stage")
    @Caching(evict = {@CacheEvict(value = CacheValue.LISTS, key = "'stageList'"),
            @CacheEvict(value = CacheValue.MAPS, key = "'stageMap'"),
            @CacheEvict(value = CacheValue.MAPS, key = "'allCodeStageMap'"),
            @CacheEvict(value = CacheValue.DROP_INFO_LIST, allEntries = true),
            @CacheEvict(value = CacheValue.LATEST_DROP_INFO_MAP, allEntries = true),
            @CacheEvict(value = CacheValue.LATEST_MAX_ACCUMULATABLE_TIME_RANGE_MAP, allEntries = true),
            @CacheEvict(value = CacheValue.LATEST_TIME_RANGE_MAP, allEntries = true),
            @CacheEvict(value = CacheValue.DROP_SET, allEntries = true)})
    public ResponseEntity<String> evictStageCache() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/time-range")
    @Caching(evict = {@CacheEvict(value = CacheValue.LISTS, key = "'timeRangeList'"),
            @CacheEvict(value = CacheValue.MAPS, key = "'timeRangeMap'")})
    public ResponseEntity<String> evictTimeRangeCache() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/notice")
    @Caching(evict = {@CacheEvict(value = CacheValue.LISTS, key = "'noticeList'")})
    public ResponseEntity<String> evictNoticeCache() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/property")
    @Caching(evict = {@CacheEvict(value = CacheValue.MAPS, key = "'propertiesMap'")})
    public ResponseEntity<String> evictSystemPropertiesCache() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/config")
    @Caching(evict = {@CacheEvict(value = CacheValue.MAPS, key = "'frontendConfigMap'")})
    public ResponseEntity<String> evictFrontendConfigsCache() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/event-period")
    @Caching(evict = {@CacheEvict(value = CacheValue.LISTS, key = "'eventPeriodList'")})
    public ResponseEntity<String> evictEventPeriodCache() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
