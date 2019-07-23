package io.penguinstats.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.model.Zone;
import io.penguinstats.service.ZoneService;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

	@Resource(name = "zoneService")
	private ZoneService zoneService;

	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Zone>> getAllZones() {
		return new ResponseEntity<List<Zone>>(zoneService.getAllZones(), HttpStatus.OK);
	}

	@GetMapping(path = "/{zoneId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Zone> getZoneByZoneId(@PathVariable("zoneId") String zoneId) {
		Zone zone = zoneService.getZoneByZoneId(zoneId);
		return new ResponseEntity<Zone>(zone, zone != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

}
