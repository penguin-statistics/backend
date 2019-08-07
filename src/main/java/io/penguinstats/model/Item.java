package io.penguinstats.model;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item implements Serializable {

	@Id
	@JsonIgnore
	private ObjectId id;
	@Indexed
	private String itemId;
	private String name;
	private Integer sortId;
	private Integer rarity;
	@Deprecated
	@JsonIgnore
	private String iconUrl;
	private String itemType;
	private Integer addTimePoint;
	private List<Integer> spriteCoord;

	public Item(String itemId, String name, Integer sortId, Integer rarity, String iconUrl, String itemType,
			Integer addTimePoint, List<Integer> spriteCoord) {
		this.itemId = itemId;
		this.name = name;
		this.sortId = sortId;
		this.rarity = rarity;
		this.iconUrl = iconUrl;
		this.itemType = itemType;
		this.addTimePoint = addTimePoint;
		this.spriteCoord = spriteCoord;
	}
}
