package io.penguinstats.bean;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class StageTimes extends Documentable {

	private int stageID;
	private String stageType;
	private List<Integer> times;

	public StageTimes() {}

	public StageTimes(int stageID, String stageType, List<Integer> times) {
		this.stageID = stageID;
		this.stageType = stageType;
		this.times = times;
	}

	@SuppressWarnings("unchecked")
	public StageTimes(Document doc) {
		this.stageID = doc.getInteger("stageID");
		this.stageType = doc.getString("stageType");
		this.times = (ArrayList<Integer>)doc.get("times");
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

	public List<Integer> getTimes() {
		return times;
	}

	public void setTimes(List<Integer> times) {
		this.times = times;
	}

	@Override
	public Document toDocument() {
		return new Document().append("stageID", this.stageID).append("times", this.times).append("stageType",
				this.stageType);
	}

}
