package io.penguinstats.util;

import java.util.HashMap;
import java.util.Map;

public class LastUpdateTimeUtil {

	private static Map<String, Long> lastUpdateTimeMap = new HashMap<>();

	public static void setCurrentTimestamp(String key) {
		lastUpdateTimeMap.put(key, System.currentTimeMillis());
	}

	public static Long getLastUpdateTime(String key) {
		Long result = lastUpdateTimeMap.get(key);
		return result == null ? -1L : lastUpdateTimeMap.get(key);
	}

}
