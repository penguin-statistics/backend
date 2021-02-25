package io.penguinstats.model;

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
@ApiModel(description = "The model for the metadata of one screenshot.")
public class ScreenshotMetadata {

    @ApiModelProperty(notes = "The name of the file.")
    private String fileName;

    @ApiModelProperty(notes = "The last modified time of the file. Time unit is milliseconds.")
    private Long lastModified;

    @ApiModelProperty(notes = "The md5 of the file.")
    private String md5;

    @ApiModelProperty(notes = "The fingerprint of the screenshot.")
    private String fingerprint;

    @ApiModelProperty(notes = "The width of the screenshot. Unit is pixels.")
    private Integer width;

    @ApiModelProperty(notes = "The height of the screenshot. Unit is pixels.")
    private Integer height;

}
