package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import io.penguinstats.model.Zone;

public interface ZoneService {

	void saveZone(Zone zone);

	Zone getZoneByZoneId(String zoneId);

	List<Zone> getAllZones();

	Map<String, Zone> getZoneMap();

}
