package io.penguinstats.controller.v2.response;

import java.io.Serializable;
import java.util.Map;

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
@ApiModel(description = "The trend result model of a specific stage.")
public class StageTrend implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "The start time of the trend result in this stage.")
	private Long startTime;

	@ApiModelProperty(notes = "A map containing trend details. Key is itemId.")
	@JsonProperty("results")
	private Map<String, TrendDetail> trendDetailMap;

}
