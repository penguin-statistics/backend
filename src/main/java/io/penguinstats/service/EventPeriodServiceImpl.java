package io.penguinstats.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.dao.EventPeriodDao;
import io.penguinstats.model.EventPeriod;
import io.penguinstats.util.LastUpdateTimeUtil;

@Service("eventPeriodService")
public class EventPeriodServiceImpl implements EventPeriodService {

	@Autowired
	private EventPeriodDao eventPeriodDao;

	@Override
	public void saveEventPeriod(EventPeriod eventPeriod) {
		eventPeriodDao.save(eventPeriod);
	}

	@Override
	public List<EventPeriod> getAllSortedEventPeriod() {
		List<EventPeriod> result = eventPeriodDao.findAll(Sort.by(Direction.ASC, "start"));
		LastUpdateTimeUtil.setCurrentTimestamp(LastUpdateMapKeyName.EVENT_PERIOD_LIST);
		return result;
	}

}
