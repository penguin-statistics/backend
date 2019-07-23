package io.penguinstats.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Document(collection = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

	@Id
	@JsonIgnore
	private ObjectId id;
	@Indexed
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

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

}
