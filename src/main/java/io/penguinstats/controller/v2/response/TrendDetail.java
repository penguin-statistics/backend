package io.penguinstats.controller.v2.response;

import java.io.Serializable;
import java.util.List;

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
@ApiModel(description = "The trend detail model of a specific item in one stage.")
public class TrendDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "The number of times you played in the stage.")
	private List<Integer> times;

	@ApiModelProperty(notes = "Quantity of the dropped item.")
	private List<Integer> quantity;

}
