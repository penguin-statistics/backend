package io.penguinstats.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.model.Stage;
import io.penguinstats.service.StageService;

@RestController
@RequestMapping("/api/stages")
public class StageController {

	@Resource(name = "stageService")
	private StageService stageService;

	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Stage>> getAllStages(@RequestParam(value = "zoneId", required = false) String zoneId) {
		return new ResponseEntity<List<Stage>>(
				zoneId == null ? stageService.getAllStages() : stageService.getStagesByZoneId(zoneId), HttpStatus.OK);
	}

	@GetMapping(path = "/{stageId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Stage> getStageByStageId(@PathVariable("stageId") String stageId) {
		Stage stage = stageService.getStageByStageId(stageId);
		return new ResponseEntity<Stage>(stage, stage != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

}
