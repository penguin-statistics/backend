package io.penguinstats.controller.v2.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.penguinstats.model.PatternMatrixElement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The response model for pattern query.")
public class PatternQueryResponse implements Serializable, BasicQueryResponse {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "All elements in the pattern matrix")
	@JsonProperty("pattern_matrix")
	private List<PatternMatrixElement> elements;

}
