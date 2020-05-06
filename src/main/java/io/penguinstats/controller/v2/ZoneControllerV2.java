package io.penguinstats.controller.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v2/zones")
public class ZoneControllerV2 {

	@Autowired
	private ZoneService zoneService;

	@ApiOperation("Get all zones")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Zone>>
			getAllZones(@RequestParam(name = "i18n", required = false, defaultValue = "false") boolean i18n) {
		List<Zone> zones = zoneService.getAllZones();
		zones.forEach(zone -> zone = i18n ? zone.toNewI18nView() : zone.toNewNonI18nView());
		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("zoneList").toString());
		return new ResponseEntity<List<Zone>>(zones, headers, HttpStatus.OK);
	}

	@ApiOperation("Get zone by zone ID")
	@GetMapping(path = "/{zoneId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Zone> getZoneByZoneId(@PathVariable("zoneId") String zoneId,
			@RequestParam(name = "i18n", required = false, defaultValue = "false") boolean i18n) {
		Zone zone = zoneService.getZoneByZoneId(zoneId);
		if (zone == null)
			return new ResponseEntity<Zone>(HttpStatus.NOT_FOUND);
		zone = i18n ? zone.toNewI18nView() : zone.toNewNonI18nView();
		return new ResponseEntity<Zone>(zone, HttpStatus.OK);
	}

}
