package io.penguinstats.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model for the element in pattern matrix.")
public class PatternMatrixElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private String stageId;

	private DropPattern pattern;

	@ApiModelProperty(notes = "The number of times this pattern has dropped")
	private Integer quantity;

	@ApiModelProperty(notes = "The number of times this stage has been played")
	private Integer times;

	@ApiModelProperty(notes = "The left end of the interval used in the calculation")
	private Long start;

	@ApiModelProperty(notes = "The right end of the interval used in the calculation")
	private Long end;

}
