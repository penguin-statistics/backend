package io.penguinstats.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.TimeRangeDao;
import io.penguinstats.model.TimeRange;
import io.penguinstats.util.LastUpdateTimeUtil;

@Service("timeRangeService")
public class TimeRangeServiceImpl implements TimeRangeService {

	@Autowired
	private TimeRangeDao timeRangeDao;

	@Override
	public void saveTimeRange(TimeRange timeRange) {
		timeRangeDao.save(timeRange);
	}

	@Override
	public TimeRange getTimeRangeByRangeID(String rangeID) {
		return timeRangeDao.findByRangeID(rangeID);
	}

	/**
	 * @Title: getAllTimeRanges
	 * @Description: Return all time ranges in the database as a list.
	 * @return List<TimeRange>
	 */
	@Override
	public List<TimeRange> getAllTimeRanges() {
		List<TimeRange> ranges = timeRangeDao.findAll();
		LastUpdateTimeUtil.setCurrentTimestamp("timeRangeList");
		return ranges;
	}

	/**
	 * @Title: getTimeRangeMap
	 * @Description: Return a map which has rangeID as key and time range object as value.
	 * @return Map<String,TimeRange>
	 */
	@Override
	public Map<String, TimeRange> getTimeRangeMap() {
		List<TimeRange> list = getAllTimeRanges();
		Map<String, TimeRange> map = new HashMap<>();
		list.forEach(range -> map.put(range.getRangeID(), range));
		return map;
	}

}
