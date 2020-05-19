package io.penguinstats.controller.v2.response;

import java.io.Serializable;
import java.util.List;

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
@ApiModel(description = "The response model for website statistical data.")
public class SiteStatsResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "The number of times each stage has been played")
	private List<StageTimes> totalStageTimes;

	@ApiModelProperty(notes = "The number of times each stage has been played in the last 24 hours")
	private List<StageTimes> totalStageTimes_24h;

	@ApiModelProperty(notes = "The number of times each item has dropped")
	private List<ItemQuantity> totalItemQuantities;

	@ApiModelProperty(notes = "The total AP cost for all stages that have been played")
	private Integer totalApCost;

}
