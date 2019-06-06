package io.penguinstats.dao;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

import io.penguinstats.bean.Zone;

public class ZoneDao extends BaseDao<Zone> {

	public ZoneDao() {
		super("zone");
	}

	public Zone findByZoneId(String zoneId) {
		MongoCursor<Document> iter = collection.find(eq("zoneId", zoneId)).iterator();
		if (iter.hasNext()) {
			Document document = iter.next();
			return new Zone(document);
		}
		return null;
	}

}
