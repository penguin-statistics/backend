package io.penguinstats.util.exception;

import io.penguinstats.enums.ErrorCode;

import java.util.Optional;

/**
 * This exception will be thrown in case of user operation error,
 * data not found in the database, or some parameters illegal, etc.
 * <p>
 * Alarm: Generally, no alarm is required.
 * Log level: INFO
 *
 * @date 2021/01/15
 */
public class BusinessException extends PenguinException {

    public BusinessException(ErrorCode errorCode, String message, Optional<Object> data, Throwable cause) {
        super(errorCode, message, data, cause);
    }

    public BusinessException(ErrorCode errorCode, String message, Optional<Object> data) {
        super(errorCode, message, data);
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message, Optional.empty());
    }
}
