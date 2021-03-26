package io.penguinstats.util.validator;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;
import io.penguinstats.service.DropInfoService;

@Order(1)
@Component("stageTimeValidator")
public class StageTimeValidator extends BaseValidator {

    @Autowired
    private DropInfoService dropInfoService;



    @Override
    public boolean validate(ValidatorContext context) {
        Server server = context.getServer();
        String stageId = context.getStageId();
        Long timestamp = context.getTimestamp();

        if (server == null || StringUtils.isEmpty(stageId) || timestamp == null)
            return false;

        Map<String, List<DropInfo>> openingDropInfosMap = dropInfoService.getOpeningDropInfosMap(server, timestamp);
        return openingDropInfosMap.containsKey(stageId);
    }

}
