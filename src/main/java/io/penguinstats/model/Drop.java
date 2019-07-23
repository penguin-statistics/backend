package io.penguinstats.model;

public class Drop {

	private String itemId;
	private int quantity;

	public Drop() {}

	public Drop(String itemId, int quantity) {
		this.itemId = itemId;
		this.quantity = quantity;
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

}
