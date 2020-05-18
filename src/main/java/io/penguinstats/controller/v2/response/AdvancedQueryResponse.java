package io.penguinstats.controller.v2.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@ApiModel(description = "The response model for advanced query.")
public class AdvancedQueryResponse implements Serializable, QueryResponse {

	private static final long serialVersionUID = 1L;

	public AdvancedQueryResponse(List<BasicQueryResponse> results) {
		this.results = results;
	}

	public AdvancedQueryResponse(String error) {
		this.error = error;
	}

	@ApiModelProperty(notes = "A list containing all query results.")
	@JsonProperty("advanced_results")
	private List<BasicQueryResponse> results;

	@ApiModelProperty(notes = "It will show up when there is something wrong with the advanced query.")
	private String error;

}
