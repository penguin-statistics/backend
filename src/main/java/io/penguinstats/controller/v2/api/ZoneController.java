package io.penguinstats.controller.v2.api;

import io.penguinstats.util.exception.NotFoundException;
import java.util.Date;
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
import io.penguinstats.util.DateUtil;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("zoneController_v2")
@RequestMapping("/api/v2/zones")
@Api(tags = {"Zone"})
public class ZoneController {

	@Autowired
	private ZoneService zoneService;

	@ApiOperation(value = "Get all Zones", notes = "Get all Zones in the DB.")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Zone>> getAllZones() {
		List<Zone> zones = zoneService.getAllZones();
		zones.forEach(zone -> zone.toNewView());
		HttpHeaders headers = new HttpHeaders();
		String lastModified =
				DateUtil.formatDate(new Date(LastUpdateTimeUtil.getLastUpdateTime(LastUpdateMapKeyName.ZONE_LIST)));
		headers.add(HttpHeaders.LAST_MODIFIED, lastModified);
		return new ResponseEntity<List<Zone>>(zones, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Get a Zone by ZoneId")
	@GetMapping(path = "/{zoneId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Zone> getZoneByZoneId(@PathVariable("zoneId") String zoneId) throws NotFoundException {
		Zone zone = zoneService.getZoneByZoneId(zoneId);
		zone.toNewView();
		return new ResponseEntity<Zone>(zone, HttpStatus.OK);
	}

}
