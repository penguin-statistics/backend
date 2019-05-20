package io.penguinstats.bean;

import org.bson.Document;
import org.json.JSONObject;

public class Material extends Item {

	private String materialCategory;

	public Material() {}

	public Material(int id, String name, String img, int rarity, String itemType, String materialCategory) {
		super(id, name, img, rarity, itemType);
		this.materialCategory = materialCategory;
	}

	public Material(Document doc) {
		this.id = doc.getInteger("id");
		this.name = doc.getString("name");
		this.img = doc.getString("img");
		this.rarity = doc.getInteger("rarity");
		this.itemType = doc.getString("itemType");
		this.materialCategory = doc.getString("materialCategory");
	}

	public String getMaterialCategory() {
		return materialCategory;
	}

	public void setMaterialCategory(String materialCategory) {
		this.materialCategory = materialCategory;
	}

	@Override
	public Document toDocument() {
		return super.toDocument().append("materialCategory", this.materialCategory);
	}

	public JSONObject asJSON() {
		return new JSONObject().put("id", this.id).put("name", this.name).put("img", this.img)
				.put("rarity", this.rarity).put("itemType", this.itemType)
				.put("materialCategory", this.materialCategory);
	}

}
