package io.penguinstats.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {

	private static Logger logger = LogManager.getLogger(JSONUtil.class);
	private static ObjectMapper mapper = new ObjectMapper();

	public static JSONObject convertObjectToJSONObject(Object o) {
		try {
			String jsonStr = mapper.writeValueAsString(o);
			return new JSONObject(jsonStr);
		} catch (JsonProcessingException e) {
			logger.error("Error in convertObjectToJSONObject: ", e);
			return null;
		}

	}

}
