package io.penguinstats.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Document(collection = "item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item {

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

	public Item() {}

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

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSortId() {
		return sortId;
	}

	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}

	public Integer getRarity() {
		return rarity;
	}

	public void setRarity(Integer rarity) {
		this.rarity = rarity;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public Integer getAddTimePoint() {
		return addTimePoint;
	}

	public void setAddTimePoint(Integer addTimePoint) {
		this.addTimePoint = addTimePoint;
	}

	public List<Integer> getSpriteCoord() {
		return spriteCoord;
	}

	public void setSpriteCoord(List<Integer> spriteCoord) {
		this.spriteCoord = spriteCoord;
	}

}
