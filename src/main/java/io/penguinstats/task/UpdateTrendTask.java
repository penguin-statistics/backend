package io.penguinstats.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.Server;
import io.penguinstats.service.ItemDropService;

@Component
public class UpdateTrendTask implements Task {

	private static Logger logger = LogManager.getLogger(UpdateTrendTask.class);

	@Autowired
	private ItemDropService itemDropService;

	@Autowired
	private ThreadPoolTaskExecutor executor;

	@Scheduled(fixedRate = 86400000)
	@Override
	public void execute() {
		logger.info("execute UpdateTrendTask");
		for (Server server : Server.values()) {
			executor
					.execute(() -> itemDropService.refreshSegmentedGlobalDropMatrixElementMap(server, 1, 30));
		}
	}

}
