package io.penguinstats.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

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

}
