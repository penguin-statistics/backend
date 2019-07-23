package io.penguinstats.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Document(collection = "zone")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Zone {

	@Id
	@JsonIgnore
	private ObjectId id;
	@Indexed
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

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

}
