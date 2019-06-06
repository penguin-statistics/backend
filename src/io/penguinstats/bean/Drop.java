package io.penguinstats.bean;

import org.bson.Document;
import org.json.JSONObject;

public class Drop extends Documentable {

	private String itemId;
	private int quantity;

	public Drop() {}

	public Drop(String itemId, int quantity) {
		this.itemId = itemId;
		this.quantity = quantity;
	}

	public Drop(Document doc) {
		this.itemId = doc.getString("itemId");
		this.quantity = doc.getInteger("quantity");
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public Document toDocument() {
		return new Document().append("itemId", this.itemId).append("quantity", this.quantity);
	}

	public JSONObject asJSON() {
		return new JSONObject().put("itemId", this.itemId).put("quantity", this.quantity);
	}

}
