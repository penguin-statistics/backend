package io.penguinstats.bean;

import org.bson.Document;
import org.json.JSONObject;

public class Item extends Documentable {

	protected int id;
	protected String name;
	protected String img;
	protected int rarity;
	protected String itemType;

	public Item() {}

	public Item(int id, String name, String img, int rarity, String itemType) {
		this.id = id;
		this.name = name;
		this.img = img;
		this.rarity = rarity;
		this.itemType = itemType;
	}

	public Item(Document doc) {
		this.id = doc.getInteger("id");
		this.name = doc.getString("name");
		this.img = doc.getString("img");
		this.rarity = doc.getInteger("rarity");
		this.itemType = doc.getString("itemType");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getRarity() {
		return rarity;
	}

	public void setRarity(int rarity) {
		this.rarity = rarity;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	@Override
	public Document toDocument() {
		return new Document().append("id", this.id).append("name", this.name).append("img", this.img)
				.append("rarity", this.rarity).append("itemType", this.itemType);
	}

	public JSONObject asJSON() {
		return new JSONObject().put("id", this.id).put("name", this.name).put("img", this.img)
				.put("rarity", this.rarity).put("itemType", this.itemType);
	}

}
