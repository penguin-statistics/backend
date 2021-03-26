package io.penguinstats.controller.v2.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
@ApiModel(description = "The request model for screenshot recognition report submitting.")
public class RecognitionReportRequest {

    @ApiModelProperty(notes = "The list of drops.")
    @NotNull
    @Valid
    private List<SingleRecognitionDrop> batchDrops;

    @ApiModelProperty(notes = "The server of these drops. Cannot be blank.")
    private Server server;

    @ApiModelProperty(
            notes = "The source of this report. It is used to mark which website/app is sending this request. Nullable (not recommended).")
    private String source;

    @ApiModelProperty(notes = "The version of the website/app. Nullable (not recommended).")
    private String version;

    @ApiModelProperty(notes = "This object is generated from decryption or not.")
    private Boolean doneDecryption;

    @ApiModelProperty(notes = "Used for verification.")
    private Long timestamp;

}
