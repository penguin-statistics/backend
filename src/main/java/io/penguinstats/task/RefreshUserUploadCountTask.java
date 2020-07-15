package io.penguinstats.task;

import io.penguinstats.util.UserUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RefreshUserUploadCountTask implements Task {

	@Autowired
	private UserUtil userUtil;

	@Scheduled(fixedRate = 3600000, initialDelay = 3600000)
	@Override
	public void execute() {
		log.info("execute RefreshUserUploadCountTask");
		userUtil.updateTwoUploadCountsForAllUsers();
	}

}
