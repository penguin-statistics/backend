package io.penguinstats.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MatrixElement is used to present a sparse matrix for drop records.<br>
 * <b>quantity</b> is how many times this item has dropped. <br>
 * <b>times</b> is how many times this stage has been played.
 * 
 * @author AlvISs_Reimu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model for the element in drop matrix.")
public class DropMatrixElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private String stageId;

	private String itemId;

	@ApiModelProperty(notes = "The number of times this item has dropped")
	private Integer quantity;

	@ApiModelProperty(notes = "The number of times this stage has been played")
	private Integer times;

	@ApiModelProperty(notes = "The left end of the interval used in the calculation")
	private Long start;

	@ApiModelProperty(notes = "The right end of the interval used in the calculation")
	private Long end;

}
