package io.penguinstats.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.penguinstats.constant.Constant.SystemPropertyKey;
import io.penguinstats.enums.Server;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.SystemPropertyService;

@Component
public class UpdateTrendTask implements Task {

	private static Logger logger = LogManager.getLogger(UpdateTrendTask.class);

	@Autowired
	private ItemDropService itemDropService;

	@Autowired
	private SystemPropertyService systemPropertyService;

	@Scheduled(fixedRate = 86400000)
	@Override
	public void execute() {
		logger.info("execute UpdateTrendTask");

		Long interval = systemPropertyService.getPropertyLongValue(SystemPropertyKey.DEFAULT_GLOBAL_TREND_INTERVAL);
		Long range = systemPropertyService.getPropertyLongValue(SystemPropertyKey.DEFAULT_GLOBAL_TREND_RANGE);

		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		for (Server server : Server.values()) {
			singleThreadExecutor
					.execute(() -> itemDropService.refreshSegmentedGlobalDropMatrixElementMap(server, interval, range));
		}
	}

}
