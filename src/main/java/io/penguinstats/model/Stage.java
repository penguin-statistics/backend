package io.penguinstats.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Document(collection = "stage_v2")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Stage {

	@Id
	@JsonIgnore
	private ObjectId id;
	private String stageType;
	@Indexed
	private String stageId;
	private String zoneId;
	private String code;
	private Integer apCost;
	private List<String> normalDrop;
	private List<String> specialDrop;
	private List<String> extraDrop;

	public Stage() {}

	public Stage(String stageType, String stageId, String zoneId, String code, Integer apCost, List<String> normalDrop,
			List<String> specialDrop, List<String> extraDrop) {
		this.stageType = stageType;
		this.stageId = stageId;
		this.zoneId = zoneId;
		this.code = code;
		this.apCost = apCost;
		this.normalDrop = normalDrop;
		this.specialDrop = specialDrop;
		this.extraDrop = extraDrop;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getStageType() {
		return stageType;
	}

	public void setStageType(String stageType) {
		this.stageType = stageType;
	}

	public String getStageId() {
		return stageId;
	}

	public void setStageId(String stageId) {
		this.stageId = stageId;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getApCost() {
		return apCost;
	}

	public void setApCost(Integer apCost) {
		this.apCost = apCost;
	}

	public List<String> getNormalDrop() {
		return normalDrop;
	}

	public void setNormalDrop(List<String> normalDrop) {
		this.normalDrop = normalDrop;
	}

	public List<String> getSpecialDrop() {
		return specialDrop;
	}

	public void setSpecialDrop(List<String> specialDrop) {
		this.specialDrop = specialDrop;
	}

	public List<String> getExtraDrop() {
		return extraDrop;
	}

	public void setExtraDrop(List<String> extraDrop) {
		this.extraDrop = extraDrop;
	}

	public Set<String> getDropsSet() {
		Set<String> set = new HashSet<>();
		if (this.normalDrop != null)
			set.addAll(this.normalDrop);
		if (this.specialDrop != null)
			set.addAll(this.specialDrop);
		if (this.extraDrop != null)
			set.addAll(this.extraDrop);
		set.add("furni");
		return set;
	}

}
