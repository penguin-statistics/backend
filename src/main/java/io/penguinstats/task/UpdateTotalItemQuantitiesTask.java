package io.penguinstats.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.Server;
import io.penguinstats.service.ItemDropService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class UpdateTotalItemQuantitiesTask implements Task {

    private static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    private ItemDropService itemDropService;

    @Scheduled(fixedRate = 3600000, initialDelay = 0)
    @Override
    public void execute() {
        log.info("execute UpdateTotalItemQuantitiesTask");

        for (Server server : Server.values()) {
            singleThreadExecutor.execute(() -> itemDropService.refreshTotalItemQuantitiesMap(server));
        }
    }

}
