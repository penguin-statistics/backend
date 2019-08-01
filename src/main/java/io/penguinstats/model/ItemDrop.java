package io.penguinstats.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "item_drop_v2")
public class ItemDrop {

	@Id
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
	private Boolean isDeleted = false;
	@Indexed
	private String source;
	private String version;
	@Indexed
	private String userID;

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

	@JsonIgnore
	public int getDropQuantity(String itemId) {
		for (Drop drop : this.drops) {
			if (drop.getItemId().equals(itemId)) {
				return drop.getQuantity();
			}
		}
		return 0;
	}

}
