package io.penguinstats.task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.Server;
import io.penguinstats.model.PatternMatrixElement;
import io.penguinstats.service.PatternMatrixElementService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class UpdatePatternMatrixTask implements Task {

    @Autowired
    private PatternMatrixElementService patternMatrixElementService;

    @Scheduled(fixedRate = 3600000, initialDelay = 2400000)
    @Override
    public void execute() {
        log.info("execute UpdatePatternMatrixTask");

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        for (Server server : Server.values()) {
            singleThreadExecutor.execute(() -> {
                List<PatternMatrixElement> elements =
                        patternMatrixElementService.generateGlobalPatternMatrixElements(server, null);
                patternMatrixElementService.batchDelete(server);
                patternMatrixElementService.batchSave(elements);
            });
        }
    }

}
