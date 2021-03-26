package io.penguinstats.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

/**
 * ErrorCode enum
 */
public enum ErrorCode {
    /**
     * Unknown error
     */
    UNKNOWN(101),

    /**
     * Invalid parameter error
     */
    INVALID_PARAMETER(400),

    /**
     * Not found error
     */
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500),
    /**
     * Can't create user account
     */
    CANNOT_CREATE_USER(1001),

    /**
     * Item drop hash not match
     */
    ITEM_DROP_HASH_ID_NOT_MATCH(2001),

    BUSINESS_EXCEPTION(3001),
    SERVICE_EXCEPTION(3002);

    private final Integer value;

    ErrorCode(Integer value) {
        this.value = value;
    }

    @JsonFormat
    public int getValue() {
        return this.value;
    }

    /**
     * read only
     */
    private static final Map<Integer, ErrorCode> FROM_VALUE_MAP = Arrays.stream(ErrorCode.values())
            .collect(toMap(ErrorCode::getValue, e -> e, (e1, e2) -> e1));

    public static ErrorCode fromValue(Integer value) {
        return Optional.ofNullable(FROM_VALUE_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values())));
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
