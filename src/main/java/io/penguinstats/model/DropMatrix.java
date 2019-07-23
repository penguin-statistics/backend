package io.penguinstats.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DropMatrix is used to present a sparse matrix for drop records.<br>
 * <b>quantity</b> is how many times this item has dropped. <br>
 * <b>times</b> is how many times this stage has been played.
 * 
 * @author AlvISs_Reimu
 */
@Document(collection = "drop_matrix_v2")
public class DropMatrix {

	@Id
	@JsonIgnore
	private ObjectId id;
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

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

}