package io.penguinstats.controller.v2.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.model.RecognitionReportError;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The response model for screenshot recognition report.")
public class RecognitionReportResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<RecognitionReportError> errors;

}
