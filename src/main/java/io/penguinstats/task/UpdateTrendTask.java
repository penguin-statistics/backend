package io.penguinstats.task;

import io.penguinstats.constant.Constant.SystemPropertyKey;
import io.penguinstats.enums.Server;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.SystemPropertyService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class UpdateTrendTask implements Task {

	@Autowired
	private ItemDropService itemDropService;

	@Autowired
	private SystemPropertyService systemPropertyService;

	@Scheduled(fixedRate = 86400000)
	@Override
	public void execute() {
		log.info("execute UpdateTrendTask");

		Long interval = systemPropertyService.getPropertyLongValue(SystemPropertyKey.DEFAULT_GLOBAL_TREND_INTERVAL);
		Long range = systemPropertyService.getPropertyLongValue(SystemPropertyKey.DEFAULT_GLOBAL_TREND_RANGE);

		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		for (Server server : Server.values()) {
			singleThreadExecutor
					.execute(() -> itemDropService.refreshSegmentedGlobalDropMatrixElements(server, interval, range));
		}
	}

}
