package io.penguinstats.controller.v2.response;

import java.io.Serializable;

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
@ApiModel(description = "The response model for a stage with its playing times.")
public class StageTimes implements Serializable {

	private static final long serialVersionUID = 1L;

	private String stageId;

	@ApiModelProperty(notes = "The number of times this stage has been played")
	private Integer times;

}
