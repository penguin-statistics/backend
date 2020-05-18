package io.penguinstats.controller.v2.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@ApiModel(description = "The request model for advanced query.")
public class AdvancedQueryRequest {

	@ApiModelProperty(notes = "The list of queries.")
	@NotNull
	@Valid
	private List<SingleQuery> queries;

}
