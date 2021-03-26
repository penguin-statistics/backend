package io.penguinstats.controller.v2.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.model.ScreenshotMetadata;
import io.penguinstats.model.TypedDrop;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model for one drop result from screenshot recognition.")
public class SingleRecognitionDrop {

	@ApiModelProperty(notes = "The stageId. Cannot be blank.")
	@NotBlank
	private String stageId;

	@ApiModelProperty(notes = "The list of dropped items.")
	@NotNull
	private List<TypedDrop> drops;

	@ApiModelProperty(notes = "The metadata of the screenshot used in this drop.")
	@NotNull
	private ScreenshotMetadata metadata;

}
