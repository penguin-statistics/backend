package io.penguinstats.util.exception;

import io.penguinstats.enums.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @see io.penguinstats.util.exception.BusinessException
 * @see io.penguinstats.util.exception.ServiceException
 */
@Deprecated
public class NotFoundException extends PenguinException {
  public NotFoundException(ErrorCode errorCode, String message,
      Optional<Object> data, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(errorCode, message, data, cause, enableSuppression, writableStackTrace);
  }

  public NotFoundException(ErrorCode errorCode, String message,
      Optional<Object> data, Throwable cause) {
    super(errorCode, message, data, cause);
  }

  public NotFoundException(ErrorCode errorCode, String message,
      Optional<Object> data) {
    super(errorCode, message, data);
  }

  public static Optional<Object> sampleData(String id) {
    Map<String, String> map = new HashMap<>();
    map.put("id", id);
    return Optional.of(map);
  }
}
