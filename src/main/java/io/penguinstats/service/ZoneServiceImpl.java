package io.penguinstats.service;

import io.penguinstats.enums.ErrorCode;
import io.penguinstats.util.exception.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.dao.ZoneDao;
import io.penguinstats.model.Stage;
import io.penguinstats.model.Zone;
import io.penguinstats.util.LastUpdateTimeUtil;

@Service("zoneService")
public class ZoneServiceImpl implements ZoneService {

	@Autowired
	private ZoneDao zoneDao;
	@Autowired
	private StageService stageService;

	@Override
	public void saveZone(Zone zone) {
		zoneDao.save(zone);
	}

	@Override
	public Zone getZoneByZoneId(String zoneId) {
		return zoneDao.findByZoneId(zoneId).orElseThrow(
				() -> new NotFoundException(ErrorCode.NOT_FOUND, "Zone[" + zoneId + "] is not found",
						Optional.of(zoneId)));
	}

	@Override
	public Zone getZoneByStageId(String stageId) {
		Stage stage = stageService.getStageByStageId(stageId);
		if (stage == null)
			return null;
		return getZoneByZoneId(stage.getZoneId());
	}

	/**
	 * @Title: getAllZones
	 * @Description: Return all zones in the database as a list.
	 * @return List<Zone>
	 */
	@Override
	public List<Zone> getAllZones() {
		List<Zone> zones = zoneDao.findAll();
		LastUpdateTimeUtil.setCurrentTimestamp(LastUpdateMapKeyName.ZONE_LIST);
		return zones;
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
