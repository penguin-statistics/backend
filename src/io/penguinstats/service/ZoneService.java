package io.penguinstats.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.penguinstats.bean.Zone;
import io.penguinstats.dao.ZoneDao;

public class ZoneService {

	private static ZoneService instance = new ZoneService();
	private static ZoneDao zoneDao = new ZoneDao();

	private ZoneService() {}

	public static ZoneService getInstance() {
		return instance;
	}

	public boolean saveZone(Zone zone) {
		return zoneDao.save(zone);
	}

	public Zone getZone(String zoneId) {
		return zoneDao.findByZoneId(zoneId);
	}

	/**
	 * @Title: getAllZones
	 * @Description: Return all zones in the database as a list.
	 * @return List<Zone>
	 */
	public List<Zone> getAllZones() {
		return zoneDao.findAll();
	}

	/**
	 * @Title: getZoneMap
	 * @Description: Return a map which has zoneId as key and zone object as value.
	 * @return Map<String,Zone>
	 */
	public Map<String, Zone> getZoneMap() {
		List<Zone> list = getAllZones();
		Map<String, Zone> map = new HashMap<>();
		list.forEach(zone -> map.put(zone.getZoneId(), zone));
		return map;
	}

}
