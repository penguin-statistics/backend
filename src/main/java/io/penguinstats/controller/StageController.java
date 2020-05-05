package io.penguinstats.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.model.Stage;
import io.penguinstats.model.Stage.StageLegacyView;
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
	public ResponseEntity<MappingJacksonValue>
			getAllStages(@RequestParam(value = "zoneId", required = false) String zoneId) {
		List<Stage> stages = zoneId == null ? stageService.getAllStages() : stageService.getStagesByZoneId(zoneId);
		MappingJacksonValue result = new MappingJacksonValue(stages);
		result.setSerializationView(StageLegacyView.class);
		if (zoneId == null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("stageList").toString());
			return new ResponseEntity<MappingJacksonValue>(result, headers, HttpStatus.OK);
		} else {
			return new ResponseEntity<MappingJacksonValue>(result, HttpStatus.OK);
		}

	}

	@ApiOperation("Get stage by stage ID")
	@GetMapping(path = "/{stageId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<MappingJacksonValue> getStageByStageId(@PathVariable("stageId") String stageId) {
		Stage stage = stageService.getStageByStageId(stageId);
		MappingJacksonValue result = new MappingJacksonValue(stage);
		result.setSerializationView(StageLegacyView.class);
		return new ResponseEntity<MappingJacksonValue>(result, stage != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@GetMapping(path = "/cache")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'stageList'"),
			@CacheEvict(value = "maps", key = "'stageMap'")})
	public ResponseEntity<String> evictStageCache() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
