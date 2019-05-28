package io.penguinstats.bean;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.json.JSONObject;

public class Chapter extends Documentable {

	private int id;
	private String name;
	private String type;
	private Long openTime;
	private Long closeTime;
	private List<Integer> stages;

	public Chapter() {}

	public Chapter(int id, String name, String type, Long openTime, Long closeTime, List<Integer> stages) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.stages = stages;
	}

	@SuppressWarnings("unchecked")
	public Chapter(Document doc) {
		this.id = doc.getInteger("id");
		this.name = doc.getString("name");
		this.type = doc.getString("type");
		this.openTime = doc.getLong("openTime");
		this.closeTime = doc.getLong("closeTime");
		this.stages = (ArrayList<Integer>)doc.get("stages");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public List<Integer> getStages() {
		return stages;
	}

	public void setStages(List<Integer> stages) {
		this.stages = stages;
	}

	@Override
	public Document toDocument() {
		return new Document().append("id", this.id).append("name", this.name).append("type", type)
				.append("openTime", openTime).append("closeTime", closeTime).append("stages", stages);
	}

	public JSONObject asJSON() {
		return new JSONObject().put("id", this.id).put("name", this.name).put("type", type).put("openTime", openTime)
				.put("closeTime", closeTime).put("stages", stages);
	}

}
