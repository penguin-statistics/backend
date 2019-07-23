package io.penguinstats.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.Zone;

@Repository(value = "zoneDao")
public class ZoneDaoImpl implements ZoneDao {

	@Resource
	private MongoTemplate mongoTemplate;

	@Override
	public void save(Zone zone) {
		mongoTemplate.save(zone);
	}

	@Override
	public void removeZone(String zoneId) {
		Query query = Query.query(Criteria.where("zoneId").is(zoneId));
		mongoTemplate.remove(query, Zone.class);
	}

	@Override
	public void updateZone(Zone zone) {
		Query query = new Query(Criteria.where("id").is(zone.getId()));

		Update update = new Update();
		update.set("zoneId", zone.getZoneId());
		update.set("zoneIndex", zone.getZoneIndex());
		update.set("type", zone.getType());
		update.set("zoneName", zone.getZoneName());
		update.set("openTime", zone.getOpenTime());
		update.set("closeTime", zone.getCloseTime());
		update.set("stages", zone.getStages());

		mongoTemplate.updateFirst(query, update, Zone.class);
	}

	@Override
	public List<Zone> findAll() {
		return mongoTemplate.findAll(Zone.class);
	}

	@Override
	public Zone findZoneByZoneId(String zoneId) {
		Query query = new Query(Criteria.where("zoneId").is(zoneId));
		Zone zone = mongoTemplate.findOne(query, Zone.class);
		return zone;
	}

}
