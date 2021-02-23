package io.penguinstats.util.exception;

import io.penguinstats.enums.ErrorCode;

import java.util.Optional;

/**
 * @see io.penguinstats.util.exception.BusinessException
 * @see io.penguinstats.util.exception.ServiceException
 */
@Deprecated
public class DatabaseException extends PenguinException {
    public DatabaseException(ErrorCode errorCode, String message,
                             Optional<Object> data, Throwable cause) {
        super(errorCode, message, data, cause);
    }

    public DatabaseException(ErrorCode errorCode, String message,
                             Optional<Object> data) {
        super(errorCode, message, data);
    }
}
