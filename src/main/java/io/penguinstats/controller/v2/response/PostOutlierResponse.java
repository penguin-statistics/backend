package io.penguinstats.controller.v2.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The response model for outlier uploading.")
public class PostOutlierResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bucket;

    private String policy;

    private String authorization;

}
