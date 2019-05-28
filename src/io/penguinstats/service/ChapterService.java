package io.penguinstats.service;

import java.util.List;

import io.penguinstats.bean.Chapter;
import io.penguinstats.dao.ChapterDao;

public class ChapterService {

	private static ChapterService instance = new ChapterService();
	private static ChapterDao dao = new ChapterDao();

	private ChapterService() {}

	public static ChapterService getInstance() {
		return instance;
	}

	public boolean saveChapter(Chapter chapter) {
		return dao.save(chapter);
	}

	public Chapter getChapter(int id) {
		return dao.findByID(id);
	}

	public List<Chapter> getAllChapters() {
		return dao.findAll();
	}

}
