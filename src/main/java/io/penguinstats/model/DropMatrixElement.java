package io.penguinstats.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DropMatrixElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private String stageId;
	private String itemId;
	private Integer quantity;
	private Integer times;
	private Long start;
	private Long end;

	public DropMatrixElement(String stageId, String itemId, Integer quantity, Integer times, Long start, Long end) {
		this.stageId = stageId;
		this.itemId = itemId;
		this.quantity = quantity;
		this.times = times;
		this.start = start;
		this.end = end;
	}

}
