package io.penguinstats.controller.v2.api;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;
import io.penguinstats.model.Stage;
import io.penguinstats.model.StageExistence;
import io.penguinstats.model.TimeRange;
import io.penguinstats.service.DropInfoService;
import io.penguinstats.service.StageService;
import io.penguinstats.service.TimeRangeService;
import io.penguinstats.util.DateUtil;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController("stageController_v2")
@RequestMapping("/api/v2/stages")
@Api(tags = {"Stage"})
public class StageController {

	@Autowired
	private StageService stageService;
	@Autowired
	private DropInfoService dropInfoService;
	@Autowired
	private TimeRangeService timeRangeService;

	@ApiOperation(value = "Get all Stages",
			notes = "Get all available Stages, including those which might not exist in the preferred server.")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Stage>>
			getAllStages(@RequestParam(name = "server", required = false, defaultValue = "CN") Server server) {
		List<Stage> stages = stageService.getAllStages();
		Map<String, List<DropInfo>> dropInfosMap =
				dropInfoService.getOpeningDropInfosMap(server, System.currentTimeMillis());
		Map<String, TimeRange> timeRangeMap = timeRangeService.getTimeRangeMap();
		Iterator<Stage> iter = stages.iterator();
		while (iter.hasNext()) {
			Stage stage = iter.next();
			List<DropInfo> infos = dropInfosMap.get(stage.getStageId());
			if (infos != null && !infos.isEmpty()) {
				TimeRange range = timeRangeMap.get(infos.get(0).getTimeRangeID());
				infos.forEach(info -> info.toStageView());
				stage.setDropInfos(infos);
				stage.setExistence(new StageExistence(range.getStart(), range.getEnd()));
			}
		}
		stages.forEach(stage -> stage.toNewView());

		Long lastUpdateTime = Math.max(LastUpdateTimeUtil.getLastUpdateTime(LastUpdateMapKeyName.STAGE_LIST),
				LastUpdateTimeUtil.getLastUpdateTime(LastUpdateMapKeyName.DROP_INFO_LIST + "_" + server));
		HttpHeaders headers = new HttpHeaders();
		String lastModified = DateUtil.formatDate(new Date(lastUpdateTime));
		headers.add(HttpHeaders.LAST_MODIFIED, lastModified);

		return new ResponseEntity<List<Stage>>(stages, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Get a Stage by Stage ID",
			notes = "Get a Stage by the specified Stage ID.")
	@GetMapping(path = "/{stageId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Stage> getStageByStageId(
			@RequestParam(name = "server", required = false, defaultValue = "CN") Server server,
			@PathVariable("stageId") String stageId) {
		Stage stage = stageService.getStageByStageId(stageId);
		if (stage == null)
			return new ResponseEntity<Stage>(HttpStatus.NOT_FOUND);
		Map<String, List<DropInfo>> dropInfosMap =
				dropInfoService.getOpeningDropInfosMap(server, System.currentTimeMillis());
		List<DropInfo> infos = dropInfosMap.get(stageId);
		if (infos != null && !infos.isEmpty()) {
			TimeRange range = timeRangeService.getTimeRangeByRangeID(infos.get(0).getTimeRangeID());
			infos.forEach(info -> info.toStageView());
			stage.setDropInfos(infos);
			stage.setExistence(new StageExistence(range.getStart(), range.getEnd()));
		}
		stage.toNewView();
		return new ResponseEntity<Stage>(stage, stage != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

}
