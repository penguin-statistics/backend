package io.penguinstats.bean;

import org.bson.Document;
import org.json.JSONObject;

public class Item extends Documentable {

	private String itemId;
	private String name;
	private Integer sortId;
	private Integer rarity;
	private String iconUrl;
	private String itemType;
	private Integer addTimePoint;

	public Item() {}

	public Item(String itemId, String name, Integer sortId, Integer rarity, String iconUrl, String itemType,
			Integer addTimePoint) {
		this.itemId = itemId;
		this.name = name;
		this.sortId = sortId;
		this.rarity = rarity;
		this.iconUrl = iconUrl;
		this.itemType = itemType;
		this.addTimePoint = addTimePoint;
	}

	public Item(Document doc) {
		this.itemId = doc.getString("itemId");
		this.name = doc.getString("name");
		this.sortId = doc.getInteger("sortId");
		this.rarity = doc.getInteger("rarity");
		this.iconUrl = doc.getString("iconUrl");
		this.itemType = doc.getString("itemType");
		this.addTimePoint = doc.getInteger("addTimePoint");
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

	@Override
	public Document toDocument() {
		return new Document().append("itemId", this.itemId).append("name", this.name).append("sortId", this.sortId)
				.append("rarity", this.rarity).append("iconUrl", this.iconUrl).append("itemType", this.itemType)
				.append("addTime", this.addTimePoint);
	}

	public JSONObject asJSON() {
		return new JSONObject().put("itemId", this.itemId).put("name", this.name).put("sortId", this.sortId)
				.put("rarity", this.rarity).put("iconUrl", this.iconUrl).put("itemType", this.itemType)
				.put("addTime", this.addTimePoint);
	}

}
