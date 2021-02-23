package io.penguinstats.controller.v2.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.penguinstats.enums.ErrorCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springfox.documentation.annotations.ApiIgnore;

@Data
@ApiIgnore
@NoArgsConstructor
public class ErrorResponseWrapper {

  @ApiModelProperty(name = "code", value = "status code", position = -2)
  @JsonInclude(Include.NON_NULL)
  private Integer code;

  @ApiModelProperty(name = "message", value = "response message", position = -1)
  @JsonInclude(Include.NON_NULL)
  private String message;

  @JsonInclude(Include.NON_NULL)
  private Object error;

  public ErrorResponseWrapper(ErrorCode errorCode, String message) {
    this.code = errorCode.getValue();
    this.message = message;
  }

  public ErrorResponseWrapper(Integer code, String message) {
    this.code = code;
    this.message = message;
  }
}
