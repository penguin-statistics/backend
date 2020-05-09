package io.penguinstats.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.Server;
import io.penguinstats.service.ItemDropService;

@Component
public class UpdateTrendTask implements Task {

	private static Logger logger = LogManager.getLogger(UpdateTrendTask.class);

	@Autowired
	private ItemDropService itemDropService;

	@Scheduled(fixedRate = 86400000)
	@Override
	public void execute() {
		logger.info("execute UpdateTrendTask");
		for (Server server : Server.values())
			itemDropService.generateSegmentedGlobalDropMatrixElementMap(server, 1, 14);
	}

}
