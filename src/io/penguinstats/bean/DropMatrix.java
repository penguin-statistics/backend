package io.penguinstats.bean;

import org.bson.Document;
import org.json.JSONObject;

/**
 * DropMatrix is used to present a sparse matrix for drop records.<br>
 * <b>quantity</b> is how many times this item has dropped. <br>
 * <b>times</b> is how many times this stage has been played.
 * 
 * @author AlvISs_Reimu
 */
public class DropMatrix extends Documentable {

	private String stageId;
	private String itemId;
	private Integer quantity;
	private Integer times;

	public DropMatrix() {}

	public DropMatrix(String stageId, String itemId, Integer quantity, Integer times) {
		this.stageId = stageId;
		this.itemId = itemId;
		this.quantity = quantity;
		this.times = times;
	}

	public DropMatrix(Document doc) {
		this.stageId = doc.getString("stageId");
		this.itemId = doc.getString("itemId");
		this.quantity = doc.getInteger("quantity");
		this.times = doc.getInteger("times");
	}

	public String getStageId() {
		return stageId;
	}

	public void setStageId(String stageId) {
		this.stageId = stageId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public void increateTimes(int num) {
		this.times += num;
	}

	public void increateQuantity(int num) {
		this.quantity += num;
	}

	@Override
	public Document toDocument() {
		return new Document().append("stageId", this.stageId).append("itemId", this.itemId)
				.append("quantity", this.quantity).append("times", this.times);
	}

	public JSONObject asJSON() {
		return new JSONObject().put("stageId", this.stageId).put("itemId", this.itemId).put("quantity", this.quantity)
				.put("times", this.times);
	}

}
