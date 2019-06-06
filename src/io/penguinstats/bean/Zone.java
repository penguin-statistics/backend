package io.penguinstats.bean;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.json.JSONObject;

public class Zone extends Documentable {

	private String zoneId;
	private Integer zoneIndex;
	private String type;
	private String zoneName;
	private Long openTime;
	private Long closeTime;
	private List<String> stages;

	public Zone() {}

	public Zone(String zoneId, Integer zoneIndex, String type, String zoneName, Long openTime, Long closeTime,
			List<String> stages) {
		this.zoneId = zoneId;
		this.zoneIndex = zoneIndex;
		this.type = type;
		this.zoneName = zoneName;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.stages = stages;
	}

	@SuppressWarnings("unchecked")
	public Zone(Document doc) {
		this.zoneId = doc.getString("zoneId");
		this.zoneIndex = doc.getInteger("zoneIndex");
		this.type = doc.getString("type");
		this.zoneName = doc.getString("zoneName");
		this.openTime = doc.getLong("openTime");
		this.closeTime = doc.getLong("closeTime");
		this.stages = (ArrayList<String>)doc.get("stages");
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public Integer getZoneIndex() {
		return zoneIndex;
	}

	public void setZoneIndex(Integer zoneIndex) {
		this.zoneIndex = zoneIndex;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public Long getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Long openTime) {
		this.openTime = openTime;
	}

	public Long getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Long closeTime) {
		this.closeTime = closeTime;
	}

	public List<String> getStages() {
		return stages;
	}

	public void setStages(List<String> stages) {
		this.stages = stages;
	}

	@Override
	public Document toDocument() {
		return new Document().append("zoneId", this.zoneId).append("zoneIndex", this.zoneIndex).append("type", type)
				.append("zoneName", this.zoneName).append("openTime", this.openTime).append("closeTime", this.closeTime)
				.append("stages", this.stages);
	}

	public JSONObject asJSON() {
		return new JSONObject().put("zoneId", this.zoneId).put("zoneIndex", this.zoneIndex).put("type", type)
				.put("zoneName", this.zoneName).put("openTime", this.openTime).put("closeTime", this.closeTime)
				.put("stages", this.stages);
	}

}
