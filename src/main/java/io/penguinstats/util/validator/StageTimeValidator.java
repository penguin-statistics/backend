package io.penguinstats.util.validator;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;
import io.penguinstats.service.DropInfoService;

@Component("stageTimeValidator")
public class StageTimeValidator extends BaseValidator {

	private DropInfoService dropInfoService;

	public StageTimeValidator(ValidatorContext context, DropInfoService dropInfoService) {
		super(context);
		this.dropInfoService = dropInfoService;
	}

	@Override
	public boolean validate() {
		Server server = this.context.getServer();
		String stageId = this.context.getStageId();
		Long timestamp = this.context.getTimestamp();

		if (server == null || StringUtils.isEmpty(stageId) || timestamp == null)
			return false;

		Map<String, List<DropInfo>> openingDropInfosMap = dropInfoService.getOpeningDropInfosMap(server, timestamp);
		return openingDropInfosMap.containsKey(stageId);
	}

}
