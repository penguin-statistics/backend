package io.penguinstats.task;

import io.penguinstats.enums.Server;
import io.penguinstats.service.ItemDropService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class UpdateDropMatrixTask implements Task {

	@Autowired
	private ItemDropService itemDropService;

	@Scheduled(fixedRate = 612345)
	@Override
	public void execute() {
		log.info("execute UpdateDropMatrixTask");

		itemDropService.updateDropMatrixElements(null, false);

		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		for (Server server : Server.values()) {
			singleThreadExecutor.execute(() -> itemDropService.refreshGlobalDropMatrixElements(server));
		}
	}

}
