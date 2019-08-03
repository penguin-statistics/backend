package io.penguinstats.model;

import lombok.Getter;
import lombok.Setter;

/**
 * WeightedMatrixElement is used to present a sparse matrix for weighted drop records.<br>
 * <b>quantity</b> is how many times this item has dropped. <br>
 * <b>times</b> is how many times this stage has been played.
 * 
 * @author AlvISs_Reimu
 */
@Getter
@Setter
public class DropMatrixElement {

	private String stageId;
	private String itemId;
	private Double quantity;
	private Double times;

	public DropMatrixElement(String stageId, String itemId, Double quantity, Double times) {
		this.stageId = stageId;
		this.itemId = itemId;
		this.quantity = quantity;
		this.times = times;
	}

}
