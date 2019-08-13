package io.penguinstats.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EvictAllSegmentedDropMatrix implements Task {

	private static Logger logger = LogManager.getLogger(EvictAllSegmentedDropMatrix.class);

	@Scheduled(fixedRate = 86400000)
	@CacheEvict(value = "all-segmented-drop-matrix", allEntries = true)
	@Override
	public void execute() {
		logger.info("execute EvictAllSegmentedDropMatrix");
	}

}
