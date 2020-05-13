package io.penguinstats.controller.v1.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant;
import io.penguinstats.constant.Constant.CustomHeader;
import io.penguinstats.model.Stage;
import io.penguinstats.service.StageService;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController("stageController_v1")
@RequestMapping("/api/stages")
public class StageController {

	@Autowired
	private StageService stageService;

	@ApiOperation("Get all stages")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Stage>> getAllStages(@RequestParam(value = "zoneId", required = false) String zoneId) {
		List<Stage> stages = zoneId == null ? stageService.getAllStages() : stageService.getStagesByZoneId(zoneId);
		stages.forEach(stage -> stage.toLegacyView());
		if (zoneId == null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("stageList").toString());
			headers.add(CustomHeader.X_PENGUIN_UPGRAGE, Constant.API_V2);
			return new ResponseEntity<List<Stage>>(stages, headers, HttpStatus.OK);
		} else {
			return new ResponseEntity<List<Stage>>(stages, HttpStatus.OK);
		}

	}

	@ApiOperation("Get stage by stage ID")
	@GetMapping(path = "/{stageId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Stage> getStageByStageId(@PathVariable("stageId") String stageId) {
		Stage stage = stageService.getStageByStageId(stageId);
		stage.toLegacyView();
		HttpHeaders headers = new HttpHeaders();
		headers.add(CustomHeader.X_PENGUIN_UPGRAGE, Constant.API_V2);
		return new ResponseEntity<Stage>(stage, headers, stage != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@GetMapping(path = "/cache")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'stageList'"),
			@CacheEvict(value = "maps", key = "'stageMap'")})
	public ResponseEntity<String> evictStageCache() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
