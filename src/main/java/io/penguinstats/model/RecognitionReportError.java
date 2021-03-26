package io.penguinstats.model;

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
@ApiModel(description = "The model for the error which happens during the screenshot recognition report.")
public class RecognitionReportError implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(notes = "The index of the error recognition result object in the batchDrops array.")
    private Integer index;

    @ApiModelProperty(notes = "The reason why this report is failed. Optional.")
    private String reason;

}
