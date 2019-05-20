package io.penguinstats.bean;

import org.bson.Document;

public class Drop extends Documentable {

	private int itemID;
	private int quantity;

	public Drop() {}

	public Drop(int itemID, int quantity) {
		this.itemID = itemID;
		this.quantity = quantity;
	}

	public Drop(Document doc) {
		this.itemID = doc.getInteger("itemID");
		this.quantity = doc.getInteger("quantity");
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public Document toDocument() {
		return new Document().append("itemID", this.itemID).append("quantity", this.quantity);
	}

}
