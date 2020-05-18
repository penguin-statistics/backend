package io.penguinstats.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.penguinstats.model.TimeRange;

public class TimeRangeUtil {

	public static List<TimeRange> combine(List<TimeRange> ranges) {
		Map<Long, TimeRange> startMap = new HashMap<>();
		ranges.forEach(range -> startMap.put(range.getStart(), range));

		Integer size;
		do {
			size = startMap.size();
			for (Long start : startMap.keySet()) {
				TimeRange range = startMap.get(start);
				Long end = range.getEnd();
				if (startMap.containsKey(end)) {
					TimeRange newRange = startMap.get(end).combine(range);
					startMap.remove(end);
					startMap.put(start, newRange);
					break;
				}
			}
		} while (startMap.size() != size);

		return new ArrayList<>(startMap.values());
	}

}
