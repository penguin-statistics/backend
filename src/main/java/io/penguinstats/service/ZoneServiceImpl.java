package io.penguinstats.service;

import io.penguinstats.dao.ZoneDao;
import io.penguinstats.model.Zone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("zoneService")
public class ZoneServiceImpl implements ZoneService {

	@Autowired
	private ZoneDao zoneDao;

	@Override
	public void saveZone(Zone zone) {
		zoneDao.save(zone);
	}

	@Override
	public Zone getZoneByZoneId(String zoneId) {
		return zoneDao.findById(zoneId).orElse(null);
	}

	/**
	 * @Title: getAllZones
	 * @Description: Return all zones in the database as a list.
	 * @return List<Zone>
	 */
	@Override
	public List<Zone> getAllZones() {
		return zoneDao.findAll();
	}

	/**
	 * @Title: getZoneMap
	 * @Description: Return a map which has zoneId as key and zone object as value.
	 * @return Map<String,Zone>
	 */
	@Override
	public Map<String, Zone> getZoneMap() {
		List<Zone> list = getAllZones();
		Map<String, Zone> map = new HashMap<>();
		list.forEach(zone -> map.put(zone.getZoneId(), zone));
		return map;
	}

}
