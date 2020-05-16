package io.penguinstats.controller.v2.request;

import javax.validation.constraints.NotBlank;

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
@ApiModel(description = "The request model for recalling last drop report.")
public class RecallLastReportRequest {

	@ApiModelProperty(notes = "The hash of the last submitted drop record.")
	@NotBlank
	private String reportHash;

}
