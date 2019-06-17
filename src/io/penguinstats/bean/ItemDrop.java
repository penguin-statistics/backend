package io.penguinstats.bean;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

public class ItemDrop extends Documentable {

	private String stageId;
	private Integer times;
	private List<Drop> drops;
	private Long timestamp;
	private String ip;
	private Boolean isReliable;
	private String source;
	private String version;

	public ItemDrop() {}

	public ItemDrop(String stageId, Integer times, List<Drop> drops, Long timestamp, String ip, Boolean isReliable,
			String source, String version) {
		this.stageId = stageId;
		this.times = times;
		this.drops = drops;
		this.timestamp = timestamp;
		this.ip = ip;
		this.isReliable = isReliable;
		this.source = source;
		this.version = version;
	}

	@SuppressWarnings("unchecked")
	public ItemDrop(Document doc) {
		this.stageId = doc.getString("stageId");
		this.times = doc.getInteger("times");
		List<Document> dropsDocList = (ArrayList<Document>)doc.get("drops");
		this.drops = new ArrayList<>();
		dropsDocList.forEach(dropDoc -> this.drops.add(new Drop(dropDoc)));
		this.timestamp = doc.getLong("timestamp");
		this.ip = doc.getString("ip");
		this.isReliable = doc.getBoolean("isReliable");
		this.source = doc.getString("source");
		this.version = doc.getString("version");
	}

	public String getStageId() {
		return stageId;
	}

	public void setStageId(String stageId) {
		this.stageId = stageId;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public List<Drop> getDrops() {
		return drops;
	}

	public void setDrops(List<Drop> drops) {
		this.drops = drops;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Boolean getIsReliable() {
		return isReliable;
	}

	public void setIsReliable(Boolean isReliable) {
		this.isReliable = isReliable;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public Document toDocument() {
		List<Document> drops = new ArrayList<>();
		for (Drop drop : this.drops) {
			drops.add(drop.toDocument());
		}
		Document doc = new Document().append("stageId", this.stageId).append("times", this.times).append("drops", drops)
				.append("ip", this.ip).append("timestamp", this.timestamp).append("isReliable", this.isReliable)
				.append("source", this.source).append("version", this.version);
		return doc;
	}

	public JSONObject asJSON() {
		JSONArray dropsArray = new JSONArray();
		for (Drop drop : this.drops) {
			dropsArray.put(drop.asJSON());
		}
		return new JSONObject().put("stageId", this.stageId).put("times", this.times).put("drops", dropsArray)
				.put("ip", this.ip).put("timestamp", this.timestamp).put("isReliable", this.isReliable)
				.put("source", this.source).put("version", this.version);
	}

	public int getDropQuantity(String itemId) {
		for (Drop drop : this.drops) {
			if (drop.getItemId().equals(itemId)) {
				return drop.getQuantity();
			}
		}
		return 0;
	}

}
