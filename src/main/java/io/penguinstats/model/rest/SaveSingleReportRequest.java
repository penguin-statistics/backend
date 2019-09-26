package io.penguinstats.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguinstats.model.Drop;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveSingleReportRequest {
    @ApiModelProperty(position = 1, required = true)
    @NotNull
    private String stageId;

    @ApiModelProperty(position = 2, required = true)
    @NotNull
    private int furnitureNum;

    @ApiModelProperty(position = 3, required = true)
    @NotNull
    private List<Drop> drops;

    private String source;
    private String version;

    @Override
    public String toString(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
