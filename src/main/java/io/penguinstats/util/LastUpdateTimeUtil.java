package io.penguinstats.util;

import java.util.HashMap;
import java.util.List;
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

	public static Long findMaxLastUpdateTime(List<String> keyNames) {
		Long lastUpdateTime = keyNames.stream().map(s -> LastUpdateTimeUtil.getLastUpdateTime(s)).distinct()
				.filter(l -> l != null).max(Long::compare).get();
		if (lastUpdateTime == null || lastUpdateTime.compareTo(0L) < 0)
			lastUpdateTime = System.currentTimeMillis();
		return lastUpdateTime;
	}

}
