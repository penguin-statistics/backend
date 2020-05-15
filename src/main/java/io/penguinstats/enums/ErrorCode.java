package io.penguinstats.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Arrays;

public enum ErrorCode {
  UNKNOWN(101),
  INVALID_PARAMETER(400),
  NOT_FOUND(404),

  CANNOT_CREATE_QUERY(1001),
  CANNOT_CREATE_VALIDATOR(1002),

  ITEM_DROP_HASH_ID_NOT_MATCH(2001),
  ;

  private Integer value;

  ErrorCode(Integer value) {
    this.value = value;
  }

  @JsonFormat
  public int getValue() {
    return this.value;
  }

  public static ErrorCode fromValue(Integer value) {
    for (ErrorCode status : values()) {
      if (status.value.equals(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException(
        "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
  }

  @Override
  public String toString() {
    return this.value.toString();
  }
}
