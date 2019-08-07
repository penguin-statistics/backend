package io.penguinstats.util;

import java.util.HashMap;
import java.util.Map;

public class LastUpdateTimeUtil {

	private static Map<String, Long> lastUpdateTimeMap = new HashMap<>();

	static {
		lastUpdateTimeMap.put("itemList", null);
		lastUpdateTimeMap.put("zoneList", null);
		lastUpdateTimeMap.put("stageList", null);
		lastUpdateTimeMap.put("extendedLimitationMap", null);
	}

	public static void setCurrentTimestamp(String key) {
		if (lastUpdateTimeMap.containsKey(key)) {
			lastUpdateTimeMap.put(key, System.currentTimeMillis());
		}
	}

	public static Long getLastUpdateTime(String key) {
		return lastUpdateTimeMap.get(key);
	}

}
