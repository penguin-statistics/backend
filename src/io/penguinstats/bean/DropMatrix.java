package io.penguinstats.bean;

import org.bson.Document;

public class DropMatrix extends Documentable {

	private int stageID;
	private String stageType;
	private int itemID;
	private int quantity;

	public DropMatrix() {}

	public DropMatrix(int stageID, String stageType, int itemID, int quantity) {
		this.stageID = stageID;
		this.stageType = stageType;
		this.itemID = itemID;
		this.quantity = quantity;
	}

	public DropMatrix(Document doc) {
		this.stageID = doc.getInteger("stageID");
		this.stageType = doc.getString("stageType");
		this.itemID = doc.getInteger("itemID");
		this.quantity = doc.getInteger("quantity");
	}

	public int getStageID() {
		return stageID;
	}

	public void setStageID(int stageID) {
		this.stageID = stageID;
	}

	public String getStageType() {
		return stageType;
	}

	public void setStageType(String stageType) {
		this.stageType = stageType;
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
		return new Document().append("stageID", this.stageID).append("itemID", this.itemID)
				.append("quantity", this.quantity).append("stageType", this.stageType);
	}

}
