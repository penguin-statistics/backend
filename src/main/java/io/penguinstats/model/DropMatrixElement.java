package io.penguinstats.model;

import java.io.Serializable;

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
public class DropMatrixElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private String stageId;
	private String itemId;
	private Integer quantity;
	private Integer times;

	public DropMatrixElement(String stageId, String itemId, Integer quantity, Integer times) {
		this.stageId = stageId;
		this.itemId = itemId;
		this.quantity = quantity;
		this.times = times;
	}

}
