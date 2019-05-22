package io.penguinstats.bean;

import org.bson.Document;

public class StageTimes extends Documentable {

	private int stageID;
	private String stageType;
	private int times;

	public StageTimes() {}

	public StageTimes(int stageID, String stageType, int times) {
		this.stageID = stageID;
		this.stageType = stageType;
		this.times = times;
	}

	public StageTimes(Document doc) {
		this.stageID = doc.getInteger("stageID");
		this.stageType = doc.getString("stageType");
		this.times = doc.getInteger("times");
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

	@Override
	public Document toDocument() {
		return new Document().append("stageID", this.stageID).append("times", this.times).append("stageType",
				this.stageType);
	}

}
