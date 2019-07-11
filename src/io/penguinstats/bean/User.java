package io.penguinstats.bean;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.json.JSONObject;

public class User extends Documentable {

	private String userID;
	private Double weight;
	private List<String> tags;
	private List<String> ips;
	private String comment;
	private Long createTime;

	public User() {}

	public User(String userID, Double weight, List<String> tags, List<String> ips, String comment, Long createTime) {
		this.userID = userID;
		this.weight = weight;
		this.tags = tags;
		this.ips = ips;
		this.comment = comment;
		this.createTime = createTime;
	}

	@SuppressWarnings("unchecked")
	public User(Document doc) {
		this.userID = doc.getString("userID");
		this.weight = doc.getDouble("weight");
		this.tags = (ArrayList<String>)doc.get("tags");
		this.ips = (ArrayList<String>)doc.get("ips");
		this.comment = doc.getString("comment");
		this.createTime = doc.getLong("createTime");
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getIps() {
		return ips;
	}

	public void setIps(List<String> ips) {
		this.ips = ips;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Override
	public Document toDocument() {
		return new Document().append("userID", this.userID).append("weight", this.weight).append("tags", tags)
				.append("ips", this.ips).append("comment", this.comment).append("createTime", this.createTime);
	}

	public JSONObject asJSON() {
		return new JSONObject().put("userID", this.userID).put("weight", this.weight).put("tags", tags)
				.put("ips", this.ips).put("comment", this.comment).put("createTime", this.createTime);
	}

}
