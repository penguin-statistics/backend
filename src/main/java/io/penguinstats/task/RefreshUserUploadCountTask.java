package io.penguinstats.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.penguinstats.util.UserUtil;

@Component
public class RefreshUserUploadCountTask implements Task {

	private static Logger logger = LogManager.getLogger(RefreshUserUploadCountTask.class);

	@Autowired
	private UserUtil userUtil;

	@Scheduled(fixedRate = 3600000)
	@Override
	public void execute() {
		logger.info("execute RefreshUserUploadCountTask");
		userUtil.updateTwoUploadCountsForAllUsers();
	}

}
