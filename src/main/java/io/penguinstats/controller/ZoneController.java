package io.penguinstats.controller;

import io.penguinstats.model.Zone;
import io.penguinstats.service.ZoneService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

	@Autowired
	private ZoneService zoneService;

	@ApiOperation("Get all zones")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Zone>> getAllZones() {
		return new ResponseEntity<List<Zone>>(zoneService.getAllZones(), HttpStatus.OK);
	}

	@ApiOperation("Get zone by zone ID")
	@GetMapping(path = "/{zoneId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Zone> getZoneByZoneId(@PathVariable("zoneId") String zoneId) {
		Zone zone = zoneService.getZoneByZoneId(zoneId);
		return new ResponseEntity<Zone>(zone, zone != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

}
