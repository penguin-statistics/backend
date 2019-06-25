package io.penguinstats.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.penguinstats.bean.Limitation;
import io.penguinstats.bean.Limitation.Bounds;
import io.penguinstats.bean.Limitation.ItemQuantityBounds;
import io.penguinstats.dao.LimitationDao;

public class LimitationService {

	private static final LimitationDao limitationDao = new LimitationDao();

	private static LimitationService instance = new LimitationService();
	private static Logger logger = LogManager.getLogger(LimitationService.class);

	public static LimitationService getInstance() {
		return instance;
	}

	public boolean saveLimitation(Limitation limitation) {
		return limitationDao.save(limitation);
	}

	public List<Limitation> getAllLimitations() {
		return limitationDao.findAll();
	}

	public static void main(String[] args) {
		LimitationService s = LimitationService.getInstance();
		List<ItemQuantityBounds> itemQuantityBounds = new ArrayList<>();
		itemQuantityBounds.add(new ItemQuantityBounds("2001", new Bounds(0, 2)));
		itemQuantityBounds.add(new ItemQuantityBounds("30062", new Bounds(0, 1)));
		Limitation limitation =
				new Limitation("main_01-12", new Bounds(0, 3), itemQuantityBounds, Arrays.asList("t1_0_1"));
		s.saveLimitation(limitation);
	}

}
