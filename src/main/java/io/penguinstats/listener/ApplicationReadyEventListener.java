package io.penguinstats.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.Server;
import io.penguinstats.service.DropMatrixElementService;
import io.penguinstats.service.PatternMatrixElementService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component("applicationReadyEventListener")
public class ApplicationReadyEventListener {

	@Autowired
	private DropMatrixElementService dropMatrixElementService;
	@Autowired
	private PatternMatrixElementService patternMatrixElementService;

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		log.info("start to load data into cache");
		for (Server server : Server.values()) {
			dropMatrixElementService.getGlobalDropMatrixElements(server, false);
			dropMatrixElementService.getGlobalDropMatrixElements(server, true);
			dropMatrixElementService.getGlobalTrendElements(server);
			patternMatrixElementService.getGlobalPatternMatrixElements(server);
		}
		log.info("load cache done");
	}

}
