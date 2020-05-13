package io.penguinstats.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WeightedMatrixElement is used to present a sparse matrix for weighted drop records.<br>
 * <b>quantity</b> is how many times this item has dropped. <br>
 * <b>times</b> is how many times this stage has been played.
 * 
 * @author AlvISs_Reimu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DropMatrixElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private String stageId;
	private String itemId;
	private Integer quantity;
	private Integer times;
	private Long start;
	private Long end;

}
