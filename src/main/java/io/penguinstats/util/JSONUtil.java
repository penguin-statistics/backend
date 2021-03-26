package io.penguinstats.util;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class JSONUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    public static JSONObject convertObjectToJSONObject(Object o) {
        try {
            String jsonStr = mapper.writeValueAsString(o);
            return new JSONObject(jsonStr);
        } catch (JsonProcessingException e) {
            log.error("Error in convertObjectToJSONObject: ", e);
            return null;
        }
    }

    public static <T> T convertJSONStrToObject(String str, Class<T> clazz) {
        try {
            return mapper.readValue(str, clazz);
        } catch (Exception e) {
            log.error("Error in convertJSONStrToObject: ", e);
            return null;
        }
    }

    public static boolean isValidJSON(final String json) {
        if (StringUtils.isAllEmpty(json))
            return false;
        boolean valid = true;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(json);
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }

}
