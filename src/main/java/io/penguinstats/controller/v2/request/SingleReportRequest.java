package io.penguinstats.controller.v2.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.Server;
import io.penguinstats.model.TypedDrop;
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
@ApiModel(description = "The request model for single drop report submitting.")
public class SingleReportRequest {

	@ApiModelProperty(notes = "The stageId. Cannot be blank.")
	@NotBlank
	private String stageId;

	@ApiModelProperty(notes = "The server of this drop. Cannot be blank.")
	private Server server;

	@ApiModelProperty(notes = "The list of dropped items.")
	@NotNull
	private List<TypedDrop> drops;

	@ApiModelProperty(
			notes = "The source of this report. It is used to mark which website/app is sending this request. Nullable (not recommended).")
	private String source;

	@ApiModelProperty(notes = "The version of the website/app. Nullable (not recommended).")
	private String version;

}
