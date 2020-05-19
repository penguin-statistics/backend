package io.penguinstats.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.Server;
import io.penguinstats.service.ItemDropService;

@Component
public class UpdateTotalItemQuantitiesTask implements Task {

	private static Logger logger = LogManager.getLogger(UpdateTotalItemQuantitiesTask.class);

	@Autowired
	private ItemDropService itemDropService;

	@Scheduled(fixedRate = 3600000, initialDelay = 300000)
	@Override
	public void execute() {
		logger.info("execute UpdateTotalItemQuantitiesTask");

		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		for (Server server : Server.values()) {
			singleThreadExecutor.execute(() -> itemDropService.refreshTotalItemQuantitiesMap(server));
		}
	}

}
