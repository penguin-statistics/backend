package io.penguinstats.controller.v2.response;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StageTrend implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long startTime;
	@JsonProperty("results")
	private Map<String, TrendDetail> trendDetailMap;

}
