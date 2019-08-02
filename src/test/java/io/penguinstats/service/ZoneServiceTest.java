package io.penguinstats.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.penguinstats.dao.ZoneDao;
import io.penguinstats.model.Zone;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZoneServiceTest {

	private static final String TEST_ZONE_ID_1 = "test_zone_id_1";
	private static final String TEST_ZONE_ID_2 = "test_zone_id_2";

	@Autowired
	private ZoneDao zoneDao;
	@Autowired
	private ZoneService zoneService;

	private List<Zone> newZones = new ArrayList<>();

	@Before
	public void setUp() {
		this.newZones.add(zoneDao.save(new Zone(TEST_ZONE_ID_1, 0, "TEST", "test1", null, null, new ArrayList<>())));
		this.newZones.add(zoneDao.save(new Zone(TEST_ZONE_ID_2, 1, "TEST", "test2", null, null, new ArrayList<>())));
	}

	@After
	public void tearDown() {
		for (Zone zone : this.newZones) {
			zoneDao.delete(zone);
		}
		this.newZones.clear();
	}

	@Test
	public void testGetZoneByZoneId() {
		Zone zone = zoneService.getZoneByZoneId(TEST_ZONE_ID_1);
		assert (TEST_ZONE_ID_1.equals(zone.getZoneId()));
	}

	@Test
	public void testGetAllZones() {
		List<Zone> zones = zoneService.getAllZones();
		int token = 2;
		for (Zone zone : zones) {
			if (TEST_ZONE_ID_1.equals(zone.getZoneId()) || TEST_ZONE_ID_2.equals(zone.getZoneId()))
				token--;
		}
		assert (token == 0);
	}

	@Test
	public void testGetZoneMap() {
		Map<String, Zone> zoneMap = zoneService.getZoneMap();
		int token = this.newZones.size();
		for (Zone zone : this.newZones) {
			Zone zoneInMap = zoneMap.get(zone.getZoneId());
			if (zoneInMap != null && zone.getId().equals(zoneInMap.getId()))
				token--;
		}
		assert (token == 0);
	}

}
