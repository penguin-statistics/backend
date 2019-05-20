package io.penguinstats.bean;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class ItemDrop extends Documentable {

	private int stageID;
	private String stageType;
	private int times;
	private List<Drop> drops;
	private long timestamp;
	private String ip;
	private int furnitureNum;

	public ItemDrop() {}

	public ItemDrop(int stageID, String stageType, int times, List<Drop> drops, long timestamp, String ip,
			int furnitureNum) {
		this.stageID = stageID;
		this.stageType = stageType;
		this.times = times;
		this.drops = drops;
		this.timestamp = timestamp;
		this.ip = ip;
		this.furnitureNum = furnitureNum;
	}

	@SuppressWarnings("unchecked")
	public ItemDrop(Document doc) {
		this.stageID = doc.getInteger("stageID");
		this.stageType = doc.getString("stageType");
		this.times = doc.getInteger("times");
		List<Document> dropsDocList = (ArrayList<Document>)doc.get("drops");
		this.drops = new ArrayList<>();
		dropsDocList.forEach(dropDoc -> this.drops.add(new Drop(dropDoc)));
		this.timestamp = doc.getLong("timestamp");
		this.ip = doc.getString("ip");
		this.furnitureNum = doc.getInteger("furnitureNum");
	}

	public int getStageID() {
		return stageID;
	}

	public void setStageID(int stageID) {
		this.stageID = stageID;
	}

	public String getStageType() {
		return stageType;
	}

	public void setStageType(String stageType) {
		this.stageType = stageType;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public List<Drop> getDrops() {
		return drops;
	}

	public void setDrops(List<Drop> drops) {
		this.drops = drops;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getFurnitureNum() {
		return furnitureNum;
	}

	public void setFurnitureNum(int furnitureNum) {
		this.furnitureNum = furnitureNum;
	}

	@Override
	public Document toDocument() {
		List<Document> drops = new ArrayList<>();
		for (Drop drop : this.drops) {
			drops.add(drop.toDocument());
		}
		return new Document().append("stageID", this.stageID).append("stageType", this.stageType).append("times", times)
				.append("drops", drops).append("ip", this.ip).append("timestamp", this.timestamp)
				.append("furnitureNum", this.furnitureNum);
	}

}
