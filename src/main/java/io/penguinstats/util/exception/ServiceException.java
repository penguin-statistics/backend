package io.penguinstats.util.exception;

import io.penguinstats.enums.ErrorCode;

import java.util.Optional;

/**
 * When there is an internal error in the system(such as calling DB timeout, RPC timeout or NPE, etc.),
 * this exception will be thrown.
 * <p>
 * Alarm: This is highly recommended
 * Log level: ERROR
 *
 * @date 2021/01/15
 */
public class ServiceException extends PenguinException {
    public ServiceException(ErrorCode errorCode, String message, Optional<Object> data, Throwable cause) {
        super(errorCode, message, data, cause);
    }

    public ServiceException(ErrorCode errorCode, String message, Optional<Object> data) {
        super(errorCode, message, data);
    }

    public ServiceException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, Optional.empty(), cause);
    }


    public ServiceException(ErrorCode errorCode, String message) {
        super(errorCode, message, Optional.empty());
    }

    public ServiceException(Throwable cause) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, cause.getMessage(), Optional.empty(), cause);
    }

    public ServiceException() {
    }
}
