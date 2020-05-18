package io.penguinstats.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.dao.NoticeDao;
import io.penguinstats.model.Notice;
import io.penguinstats.util.LastUpdateTimeUtil;

@Service("noticeService")
public class NoticeServiceImpl implements NoticeService {

	@Autowired
	private NoticeDao noticeDao;

	@Override
	public void saveNotice(Notice notice) {
		noticeDao.save(notice);
	}

	@Override
	public List<Notice> getAllNotice() {
		List<Notice> result = noticeDao.findAll();
		LastUpdateTimeUtil.setCurrentTimestamp(LastUpdateMapKeyName.NOTICE_LIST);
		return result;
	}

}
