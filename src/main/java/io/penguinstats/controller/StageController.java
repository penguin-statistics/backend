package io.penguinstats.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.model.Stage;
import io.penguinstats.service.StageService;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/stages")
public class StageController {

	@Autowired
	private StageService stageService;

	@ApiOperation("Get all stages")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Stage>> getAllStages(@RequestParam(value = "zoneId", required = false) String zoneId) {
		List<Stage> stages = zoneId == null ? stageService.getAllStages() : stageService.getStagesByZoneId(zoneId);
		if (zoneId == null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("stageList").toString());
			return new ResponseEntity<List<Stage>>(stages, headers, HttpStatus.OK);
		} else {
			return new ResponseEntity<List<Stage>>(stages, HttpStatus.OK);
		}

	}

	@ApiOperation("Get stage by stage ID")
	@GetMapping(path = "/{stageId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Stage> getStageByStageId(@PathVariable("stageId") String stageId) {
		Stage stage = stageService.getStageByStageId(stageId);
		return new ResponseEntity<Stage>(stage, stage != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

}
