package io.penguinstats.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.json.JSONObject;

public class Stage extends Documentable {

	private String stageType;
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

	@SuppressWarnings("unchecked")
	public Stage(Document doc) {
		this.stageType = doc.getString("stageType");
		this.stageId = doc.getString("stageId");
		this.zoneId = doc.getString("zoneId");
		this.code = doc.getString("code");
		this.apCost = doc.getInteger("apCost");
		this.normalDrop = (ArrayList<String>)doc.get("normalDrop");
		this.specialDrop = (ArrayList<String>)doc.get("specialDrop");
		this.extraDrop = (ArrayList<String>)doc.get("extraDrop");
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

	@Override
	public Document toDocument() {
		return new Document().append("stageType", this.stageType).append("stageId", this.stageId)
				.append("zoneId", this.zoneId).append("code", this.code).append("apCost", this.apCost)
				.append("normalDrop", this.normalDrop).append("specialDrop", this.specialDrop)
				.append("extraDrop", this.extraDrop);
	}

	public JSONObject asJSON() {
		return new JSONObject().put("stageType", this.stageType).put("stageId", this.stageId).put("zoneId", this.zoneId)
				.put("code", this.code).put("apCost", this.apCost).put("normalDrop", this.normalDrop)
				.put("specialDrop", this.specialDrop).put("extraDrop", this.extraDrop);
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
