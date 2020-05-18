package io.penguinstats.controller.v2.response;

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
@ApiModel(description = "The response model for single drop submit.")
public class SingleReportResponse {

	@ApiModelProperty(notes = "The hash of the last submitted drop record. It can be used in recall.")
	private String reportHash;

}
