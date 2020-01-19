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
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "zone")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Zone implements Serializable {

	public interface ZoneBaseView {};

	public interface ZoneI18nView extends ZoneBaseView {};

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;

	@Indexed
	@JsonView(ZoneBaseView.class)
	private String zoneId;

	@JsonView(ZoneBaseView.class)
	private Integer zoneIndex;

	@JsonView(ZoneBaseView.class)
	private String type;

	@JsonView(ZoneBaseView.class)
	private String zoneName;

	@JsonProperty("zoneName_i18n")
	@JsonView(ZoneI18nView.class)
	private Map<String, String> zoneNameMap;

	@JsonView(ZoneBaseView.class)
	private Long openTime;

	@JsonView(ZoneBaseView.class)
	private Long closeTime;

	@JsonView(ZoneBaseView.class)
	private List<String> stages;

	public Zone(String zoneId, Integer zoneIndex, String type, String zoneName, Map<String, String> zoneNameMap,
			Long openTime, Long closeTime, List<String> stages) {
		this.zoneId = zoneId;
		this.zoneIndex = zoneIndex;
		this.type = type;
		this.zoneName = zoneName;
		this.zoneNameMap = zoneNameMap;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.stages = stages;
	}

	@JsonIgnore
	public boolean isInTimeRange(long timestamp) {
		if (this.openTime != null && this.openTime.compareTo(timestamp) > 0)
			return false;
		if (this.closeTime != null && this.closeTime.compareTo(timestamp) < 0)
			return false;
		return true;
	}
}
