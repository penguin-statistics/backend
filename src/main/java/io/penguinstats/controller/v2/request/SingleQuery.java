package io.penguinstats.controller.v2.request;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.Server;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model for basic single query.")
public class SingleQuery {

	@ApiModelProperty(notes = "The server to be queried. Cannot be null.")
	private Server server;

	@ApiModelProperty(notes = "The stage to be queried. Cannot be blank.")
	@NotBlank
	private String stageId;

	@ApiModelProperty(
			notes = "Result filter by itemIds. If empty, no filter will be apllied. Otherwise, only items in this list will show in the result.")
	private List<String> itemIds;

	@ApiModelProperty(notes = "The start time of the query time range. Nullable.")
	private Long start;

	@ApiModelProperty(notes = "The end time of the query time range. Nullable.")
	private Long end;

	@ApiModelProperty(
			notes = "If null, then this query will be \"matrix query\"; Otherwise, it will be \"trend query\" based on the given interval (the length of each section, unit is millisecond).")
	private Long interval;

	@ApiModelProperty(notes = "Indicate whether showing personal data.")
	private Boolean isPersonal;

}
