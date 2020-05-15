package io.penguinstats.util;

import io.penguinstats.controller.v2.response.ErrorResponseWrapper;
import io.penguinstats.enums.ErrorCode;
import io.penguinstats.util.exception.DatabaseException;
import io.penguinstats.util.exception.NotFoundException;
import io.penguinstats.util.exception.PenguinException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

@ControllerAdvice
@Log4j2
public class RestExceptionHandler {

  @ExceptionHandler({RuntimeException.class})
  public final ResponseEntity<ErrorResponseWrapper> handleException(PenguinException ex,
      WebRequest request) {
    log.debug("encountered error", ex);
    HttpHeaders headers = new HttpHeaders();

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    ErrorResponseWrapper errorResponse = null;
    if (ex instanceof NotFoundException) {
      status = HttpStatus.NOT_FOUND;
      errorResponse = new ErrorResponseWrapper(ex.getErrorCode().getValue(), ex.getMessage());
    } else {
      errorResponse = new ErrorResponseWrapper(ErrorCode.UNKNOWN, ex.getMessage());
    }
    if (ex.getData() != null) {
      errorResponse.setError(ex.getData());
    }

    return handleExceptionInternal(ex, errorResponse, headers, status, request);
  }

  protected ResponseEntity<ErrorResponseWrapper> handleExceptionInternal(Exception ex,
      ErrorResponseWrapper body,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
      request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
    }

    return new ResponseEntity<>(body, headers, status);
  }


  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handle(Exception ex, HttpServletRequest request,
      HttpServletResponse response) {
    log.error("unexpected exception: ", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            String.format("{\"code\":%s,\"message\":\"%s\",\"stacktrace\":\"%s\"}",
                    500,
                    ex.getMessage(),
                    ExceptionUtils.getStackTrace(ex)
                ));
  }

  @ExceptionHandler({DatabaseException.class})
  public final ResponseEntity<ErrorResponseWrapper> handleDatabaseException(DatabaseException ex,
      WebRequest request) {
    log.debug("encountered error", ex);
    HttpHeaders headers = new HttpHeaders();

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    ErrorResponseWrapper apiResponse = new ErrorResponseWrapper(ex.getErrorCode().getValue(), ex.getMessage());

    if (ex.getData() != null) {
      apiResponse.setError(ex.getData());
    }

    return handleExceptionInternal(ex, apiResponse, headers, status, request);
  }

  @ExceptionHandler({IllegalArgumentException.class})
  public final ResponseEntity<? extends ErrorResponseWrapper> handleBadRequestException(
      IllegalArgumentException ex, WebRequest request) throws IOException {
    log.debug("encountered error", ex);
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    ErrorResponseWrapper apiResponse = new ErrorResponseWrapper(ErrorCode.INVALID_PARAMETER, ex.getMessage());

    apiResponse.setError(ex.getCause());

    return handleExceptionInternal(ex, apiResponse, headers, status, request);
  }
}
