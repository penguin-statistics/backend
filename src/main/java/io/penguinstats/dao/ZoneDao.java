package io.penguinstats.dao;

import io.penguinstats.model.Zone;

public interface ZoneDao extends BaseDao<Zone> {

	void removeZone(String zoneId);

	void updateZone(Zone zone);

	Zone findZoneByZoneId(String zoneId);

}
