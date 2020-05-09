package io.penguinstats.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.penguinstats.enums.Server;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "item_drop_v2")
public class ItemDrop {

	@Id
	private ObjectId id;
	@Indexed
	private String stageId;
	@Indexed
	private Server server;
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

	@JsonIgnore
	public int getDropQuantity(String itemId) {
		for (Drop drop : this.drops) {
			if (drop.getItemId().equals(itemId)) {
				return drop.getQuantity();
			}
		}
		return 0;
	}

	@JsonIgnore
	public ItemDrop toNoIDView() {
		this.id = null;
		return this;
	}

}
