package io.penguinstats.model;

public class ItemQuantityBounds {

	private String itemId;
	private Bounds bounds;

	public ItemQuantityBounds() {}

	public ItemQuantityBounds(String itemId, Bounds bounds) {
		this.itemId = itemId;
		this.bounds = bounds;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.itemId).append(" [").append(this.bounds.toString()).append("]");
		return sb.toString();
	}

}
