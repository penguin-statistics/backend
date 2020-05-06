package io.penguinstats.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.penguinstats.enums.Server;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "zone")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Zone implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;
	@Indexed
	private String zoneId;
	private Integer zoneIndex;
	private String type;
	private String zoneName;
	@JsonProperty("zoneName_i18n")
	private Map<Server, String> zoneNameMap;
	private Map<Server, ZoneExistence> existence;
	private List<String> stages;
	private Long openTime;
	private Long closeTime;

	public Zone(String zoneId, Integer zoneIndex, String type, String zoneName, Map<Server, String> zoneNameMap,
			Map<Server, ZoneExistence> existence, Long openTime, Long closeTime, List<String> stages) {
		this.zoneId = zoneId;
		this.zoneIndex = zoneIndex;
		this.type = type;
		this.zoneName = zoneName;
		this.zoneNameMap = zoneNameMap;
		this.existence = existence;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.stages = stages;
	}

	@JsonIgnore
	@Deprecated
	public boolean isInTimeRange(long timestamp) {
		if (this.openTime != null && this.openTime.compareTo(timestamp) > 0)
			return false;
		if (this.closeTime != null && this.closeTime.compareTo(timestamp) < 0)
			return false;
		return true;
	}

	@JsonIgnore
	public Zone toLegacyNonI18nView() {
		this.existence = null;
		this.zoneNameMap = null;
		return this;
	}

	@JsonIgnore
	public Zone toLegacyI18nView() {
		this.existence = null;
		return this;
	}

	@JsonIgnore
	public Zone toNewNonI18nView() {
		this.zoneNameMap = null;
		this.openTime = null;
		this.closeTime = null;
		return this;
	}

	@JsonIgnore
	public Zone toNewI18nView() {
		this.openTime = null;
		this.closeTime = null;
		return this;
	}

}
