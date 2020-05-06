package io.penguinstats.model;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StageTrend implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long startTime;
	@JsonProperty("results")
	private Map<String, TrendDetail> trendDetailMap;

}
