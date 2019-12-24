package io.penguinstats.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.penguinstats.service.ItemDropService;

@Component
public class UpdateDropMatrixTask implements Task {

	private static Logger logger = LogManager.getLogger(UpdateDropMatrixTask.class);

	@Autowired
	private ItemDropService itemDropService;

	@Scheduled(fixedRate = 600000)
	@Override
	public void execute() {
		logger.info("execute UpdateDropMatrixTask");
		itemDropService.updateDropMatrixElements(null, false);
	}

}
