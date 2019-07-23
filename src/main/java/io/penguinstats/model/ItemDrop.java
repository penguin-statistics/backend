package io.penguinstats.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "item_drop_v2")
public class ItemDrop {

	@Id
	@JsonIgnore
	private ObjectId id;
	@Indexed
	private String stageId;
	private Integer times;
	private List<Drop> drops;
	@Indexed
	private Long timestamp;
	private String ip;
	@Indexed
	private Boolean isReliable;
	@Indexed
	private String source;
	private String version;
	@Indexed
	private String userID;

	public ItemDrop() {}

	public ItemDrop(String stageId, Integer times, List<Drop> drops, Long timestamp, String ip, Boolean isReliable,
			String source, String version, String userID) {
		this.stageId = stageId;
		this.times = times;
		this.drops = drops;
		this.timestamp = timestamp;
		this.ip = ip;
		this.isReliable = isReliable;
		this.source = source;
		this.version = version;
		this.userID = userID;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
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
