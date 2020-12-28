package io.penguinstats.util;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationUtil {

    private ValidationUtil() {
    }

    /**
     * Convert field validation errors to {@link Map}
     * key:value = field:message
     *
     * @param fieldErrors Field error list
     * @return If it returns emptyMap, it means no error occurred
     */
    public static Map<String, String> mapWithFieldError(@Nullable List<FieldError> fieldErrors) {
        if (CollectionUtils.isEmpty(fieldErrors)) {
            return Collections.emptyMap();
        }

        Map<String, String> errMap = new HashMap<>(4);
        fieldErrors.forEach(filedError -> errMap.put(filedError.getField(), filedError.getDefaultMessage()));
        return errMap;
    }

    /**
     * Convert field validation errors to {@link String}
     *
     * @param fieldErrors Field error list
     * @return If it returns empty string, it means no error occurred
     */
    public static String fieldErrorToString(@Nullable List<FieldError> fieldErrors) {
        if (CollectionUtils.isEmpty(fieldErrors)) {
            return "";
        }
        StringBuilder sbuf = new StringBuilder();

        final int length = fieldErrors.size();

        for (int i = 0; i < length; i++) {
            FieldError fieldError = fieldErrors.get(i);
            sbuf.append(fieldError.getField())
                    .append(":")
                    .append(fieldError.getDefaultMessage());
            if (i == length - 1) {
                sbuf.append(".");
            } else {
                sbuf.append(";");
            }
        }

        return sbuf.toString();
    }

}
