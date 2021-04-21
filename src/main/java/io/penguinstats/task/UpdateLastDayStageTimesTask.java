package io.penguinstats.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.Server;
import io.penguinstats.service.ItemDropService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class UpdateLastDayStageTimesTask implements Task {

    private static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    private ItemDropService itemDropService;

    @Scheduled(fixedRate = 900000, initialDelay = 0)
    @Override
    public void execute() {
        log.info("execute UpdateLastDayStageTimesTask");

        for (Server server : Server.values()) {
            singleThreadExecutor
                    .execute(() -> itemDropService.refreshTotalStageTimesMap(server, TimeUnit.DAYS.toMillis(1)));
        }
    }

}
