package io.penguinstats.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.model.Zone;
import io.penguinstats.service.ZoneService;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

	@Autowired
	private ZoneService zoneService;

	@ApiOperation("Get all zones")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<MappingJacksonValue>
			getAllZones(@RequestParam(name = "i18n", required = false, defaultValue = "false") boolean i18n) {
		List<Zone> zones = zoneService.getAllZones();
		MappingJacksonValue result = new MappingJacksonValue(zones);
		result.setSerializationView(i18n ? Zone.ZoneI18nView.class : Zone.ZoneBaseView.class);
		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("zoneList").toString());
		return new ResponseEntity<MappingJacksonValue>(result, headers, HttpStatus.OK);
	}

	@ApiOperation("Get zone by zone ID")
	@GetMapping(path = "/{zoneId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<MappingJacksonValue> getZoneByZoneId(@PathVariable("zoneId") String zoneId,
			@RequestParam(name = "i18n", required = false, defaultValue = "false") boolean i18n) {
		Zone zone = zoneService.getZoneByZoneId(zoneId);
		if (zone == null)
			return new ResponseEntity<MappingJacksonValue>(HttpStatus.NOT_FOUND);

		MappingJacksonValue result = new MappingJacksonValue(zone);
		result.setSerializationView(i18n ? Zone.ZoneI18nView.class : Zone.ZoneBaseView.class);
		return new ResponseEntity<MappingJacksonValue>(result, HttpStatus.OK);
	}

	@GetMapping(path = "/cache")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'zoneList'"), @CacheEvict(value = "maps", key = "'zoneMap'")})
	public ResponseEntity<String> evictZoneCache() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
