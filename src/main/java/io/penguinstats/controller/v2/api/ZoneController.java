package io.penguinstats.controller.v2.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.model.Zone;
import io.penguinstats.service.ZoneService;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController("zoneController_v2")
@RequestMapping("/api/v2/zones")
public class ZoneController {

	@Autowired
	private ZoneService zoneService;

	@ApiOperation("Get all zones")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Zone>> getAllZones() {
		List<Zone> zones = zoneService.getAllZones();
		zones.forEach(zone -> zone.toNewView());
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.LAST_MODIFIED,
				LastUpdateTimeUtil.getLastUpdateTime(LastUpdateMapKeyName.ZONE_LIST).toString());
		return new ResponseEntity<List<Zone>>(zones, headers, HttpStatus.OK);
	}

	@ApiOperation("Get zone by zone ID")
	@GetMapping(path = "/{zoneId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Zone> getZoneByZoneId(@PathVariable("zoneId") String zoneId) {
		Zone zone = zoneService.getZoneByZoneId(zoneId);
		if (zone == null)
			return new ResponseEntity<Zone>(HttpStatus.NOT_FOUND);
		zone.toNewView();
		return new ResponseEntity<Zone>(zone, HttpStatus.OK);
	}

}
