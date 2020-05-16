package io.penguinstats.controller.v2.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvancedQueryResponse implements Serializable, QueryResponse {

	private static final long serialVersionUID = 1L;

	public AdvancedQueryResponse(List<BasicQueryResponse> results) {
		this.results = results;
	}

	public AdvancedQueryResponse(String error) {
		this.error = error;
	}

	@JsonProperty("advanced_results")
	private List<BasicQueryResponse> results;

	private String error;

}
