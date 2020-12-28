package io.penguinstats.controller.v2.api;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;
import io.penguinstats.model.Stage;
import io.penguinstats.service.DropInfoService;
import io.penguinstats.service.StageService;
import io.penguinstats.util.DateUtil;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.Api;
import io.penguinstats.util.exception.NotFoundException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("stageController_v2")
@RequestMapping("/api/v2/stages")
@Api(tags = {"Stage"})
public class StageController {

	@Autowired
	private StageService stageService;
	@Autowired
	private DropInfoService dropInfoService;

	@ApiOperation(value = "Get all Stages",
			notes = "Get all Stages in the DB, together with their current drop infos (if applicable).")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Stage>>
			getAllStages(@ApiParam(value = "Indicate which server you want to query. Default is CN.",
					required = false) @RequestParam(name = "server", required = false,
							defaultValue = "CN") Server server) {
		List<Stage> stages = stageService.getAllStages();
		Map<String, List<DropInfo>> dropInfosMap =
				dropInfoService.getOpeningDropInfosMap(server, System.currentTimeMillis());
		Iterator<Stage> iter = stages.iterator();
		while (iter.hasNext()) {
			Stage stage = iter.next();
			List<DropInfo> infos = dropInfosMap.get(stage.getStageId());
			if (infos != null && !infos.isEmpty()) {
				infos.forEach(info -> info.toStageView());
				stage.setDropInfos(infos);
			}
		}
		stages.forEach(Stage::toNewView);

		Long lastUpdateTime = Math.max(LastUpdateTimeUtil.getLastUpdateTime(LastUpdateMapKeyName.STAGE_LIST),
				LastUpdateTimeUtil.getLastUpdateTime(LastUpdateMapKeyName.DROP_INFO_LIST + "_" + server));
		HttpHeaders headers = new HttpHeaders();
		String lastModified = DateUtil.formatDate(new Date(lastUpdateTime));
		headers.add(HttpHeaders.LAST_MODIFIED, lastModified);

		return new ResponseEntity<List<Stage>>(stages, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Get Stage by StageId")
	@GetMapping(path = "/{stageId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Stage>
			getStageByStageId(@ApiParam(value = "Indicate which server you want to query. Default is CN.",
					required = false) @RequestParam(name = "server", required = false,
							defaultValue = "CN") Server server,
					@PathVariable("stageId") String stageId) throws NotFoundException {
		Stage stage = stageService.getStageByStageId(stageId);
		Map<String, List<DropInfo>> dropInfosMap =
				dropInfoService.getOpeningDropInfosMap(server, System.currentTimeMillis());
		List<DropInfo> infos = dropInfosMap.get(stageId);
		if (infos != null && !infos.isEmpty()) {
			infos.forEach(info -> info.toStageView());
			stage.setDropInfos(infos);
		}
		stage.toNewView();
		return new ResponseEntity<Stage>(stage, HttpStatus.OK);
	}

}
