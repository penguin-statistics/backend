package io.penguinstats.task;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.DropMatrixElementType;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.TimeRange;
import io.penguinstats.service.DropInfoService;
import io.penguinstats.service.DropMatrixElementService;
import io.penguinstats.service.TimeRangeService;
import io.penguinstats.util.misc.DirtyStages;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class UpdateDropMatrixTask implements Task {

    @Autowired
    private DropMatrixElementService dropMatrixElementService;
    @Autowired
    private TimeRangeService timeRangeService;
    @Autowired
    private DropInfoService dropInfoService;
    @Autowired
    private DirtyStages dirtyStages;

    @Scheduled(fixedRate = 3600000, initialDelay = 0)
    @Override
    public void execute() {
        log.info("execute UpdateDropMatrixTask");

        List<TimeRange> timeRanges = timeRangeService.getPassedTimeRanges(TimeUnit.HOURS.toMillis(1L));

        for (Server server : Server.values()) {
            if (server != Server.CN) {
                continue;
            }
            for (TimeRange range : timeRanges) {
                List<DropInfo> infos = dropInfoService.getDropInfosByServerAndTimeRangeID(server, range.getRangeID());
                Set<String> dirtyStageIds = infos.stream().map(DropInfo::getStageId).collect(Collectors.toSet());
                dirtyStages.addStageIds(server, dirtyStageIds);
            }
            log.info("Dirty stages in server {}: {}", server, dirtyStages.getStageIds(server).toString());

            List<DropMatrixElement> currentElements =
                    dropMatrixElementService.generateGlobalDropMatrixElements(server, null, false);
            dropMatrixElementService.batchDelete(DropMatrixElementType.REGULAR, server, false);
            dropMatrixElementService.batchSave(currentElements);

            List<DropMatrixElement> elements =
                    dropMatrixElementService.generateGlobalDropMatrixElements(server, null, true);
            dropMatrixElementService.batchDelete(DropMatrixElementType.REGULAR, server, true);
            dropMatrixElementService.batchSave(elements);
            dirtyStages.clear(server);
        }
    }

}
