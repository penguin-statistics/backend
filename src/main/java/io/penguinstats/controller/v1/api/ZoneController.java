package io.penguinstats.controller.v1.api;

import io.penguinstats.util.exception.NotFoundException;
import java.util.List;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant;
import io.penguinstats.constant.Constant.CustomHeader;
import io.penguinstats.model.Zone;
import io.penguinstats.service.ZoneService;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController("zoneController_v1")
@RequestMapping("/api/zones")
@Api(tags = {"@ Deprecated APIs"})
@Deprecated
public class ZoneController {

	@Autowired
	private ZoneService zoneService;

	@ApiOperation("Get all zones")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Zone>>
			getAllZones(@RequestParam(name = "i18n", required = false, defaultValue = "false") boolean i18n) {
		List<Zone> zones = zoneService.getAllZones();
		zones.forEach(zone -> zone = i18n ? zone.toLegacyI18nView() : zone.toLegacyNonI18nView());
		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("zoneList").toString());
		headers.add(CustomHeader.X_PENGUIN_UPGRAGE, Constant.API_V2);
		return new ResponseEntity<List<Zone>>(zones, headers, HttpStatus.OK);
	}

	@ApiOperation("Get zone by zone ID")
	@GetMapping(path = "/{zoneId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Zone> getZoneByZoneId(@PathVariable("zoneId") String zoneId,
			@RequestParam(name = "i18n", required = false, defaultValue = "false") boolean i18n) throws NotFoundException {
		Zone zone = zoneService.getZoneByZoneId(zoneId);
		zone = i18n ? zone.toLegacyI18nView() : zone.toLegacyNonI18nView();
		HttpHeaders headers = new HttpHeaders();
		headers.add(CustomHeader.X_PENGUIN_UPGRAGE, Constant.API_V2);
		return new ResponseEntity<Zone>(zone, headers, HttpStatus.OK);
	}

	@GetMapping(path = "/cache")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'zoneList'"), @CacheEvict(value = "maps", key = "'zoneMap'")})
	public ResponseEntity<String> evictZoneCache() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
