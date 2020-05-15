package io.penguinstats.util.exception;

import io.penguinstats.enums.ErrorCode;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class PenguinException extends RuntimeException {
  @Getter
  private ErrorCode errorCode;
  @Getter
  private Object data;

  public PenguinException(ErrorCode errorCode, String message, Optional<Object> data, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.errorCode = errorCode;
    setData(data);
  }

  public PenguinException(ErrorCode errorCode, String message, Optional<Object> data,
      Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
    setData(data);
  }

  public PenguinException(ErrorCode errorCode, String message, Optional<Object> data) {
    super(message);
    this.errorCode = errorCode;
    setData(data);
  }

  protected void setData(Optional<Object> data) {
    if (data.isPresent()) {
      this.data = data.get();
    }
  }
}
