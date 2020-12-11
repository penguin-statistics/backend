package io.penguinstats.task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.DropMatrixElementType;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.service.DropMatrixElementService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class UpdatePastDropMatrixTask implements Task {

	@Autowired
	private DropMatrixElementService dropMatrixElementService;

	@Scheduled(fixedRate = 43200000, initialDelay = 900000)
	@Override
	public void execute() {
		log.info("execute UpdatePastDropMatrixTask");

		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		for (Server server : Server.values()) {
			singleThreadExecutor.execute(() -> {
				List<DropMatrixElement> elements =
						dropMatrixElementService.generateGlobalDropMatrixElements(server, null, true);
				dropMatrixElementService.batchDelete(DropMatrixElementType.REGULAR, server, true);
				dropMatrixElementService.batchSave(elements);
			});
		}
	}

}
