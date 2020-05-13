package io.penguinstats.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.dao.NoticeDao;
import io.penguinstats.model.Notice;
import io.penguinstats.util.LastUpdateTimeUtil;

@Service("noticeService")
public class NoticeServiceImpl implements NoticeService {

	@Autowired
	private NoticeDao noticeDao;
	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public void saveNotice(Notice notice) {
		noticeDao.save(notice);
	}

	@Override
	public List<Notice> getAllSortedNotice() {
		List<Notice> result = noticeDao.findAll(new Sort(Direction.DESC, "conditions.start", "conditions.end"));
		LastUpdateTimeUtil.setCurrentTimestamp(LastUpdateMapKeyName.NOTICE_LIST);
		return result;
	}

	@Override
	public List<Notice> getAvailableNotice(Long time) {
		List<Notice> list = getSpringProxy().getAllSortedNotice();
		return list.stream().filter(notice -> {
			if (notice.getConditions() == null)
				return false;
			return Optional.ofNullable(notice.getConditions().getStart()).map(start -> (start.compareTo(time) < 0))
					.orElse(true)
					&& Optional.ofNullable(notice.getConditions().getEnd()).map(end -> (end.compareTo(time) > 0))
							.orElse(true);
		}).collect(Collectors.toList());
	}

	@Override
	public Notice getLatestNotice() {
		List<Notice> list = getSpringProxy().getAllSortedNotice();
		return list.isEmpty() ? null : list.get(0);
	}

	/**
	 * @Title: getSpringProxy
	 * @Description: Use proxy to hit cache
	 * @return NoticeService
	 */
	private NoticeService getSpringProxy() {
		return applicationContext.getBean(NoticeService.class);
	}

}
