package io.penguinstats.controller.v2.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.penguinstats.model.DropMatrixElement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author AlvISsReimu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The response model for matrix query.")
public class MatrixQueryResponse implements Serializable, BasicQueryResponse {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "All elements in the result matrix")
	@JsonProperty("matrix")
	private List<DropMatrixElement> elements;

}
