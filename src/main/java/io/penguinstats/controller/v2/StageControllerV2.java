package io.penguinstats.controller.v2;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import io.penguinstats.enums.Server;
import io.penguinstats.model.DropInfo;
import io.penguinstats.model.Stage;
import io.penguinstats.model.Stage.StageNewView;
import io.penguinstats.service.DropInfoService;
import io.penguinstats.service.StageService;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v2/stages")
public class StageControllerV2 {

	@Autowired
	private StageService stageService;
	@Autowired
	private DropInfoService dropInfoService;

	@ApiOperation("Get all stages")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<MappingJacksonValue>
			getAllStages(@RequestParam(name = "server", required = false, defaultValue = "CN") Server server) {
		List<Stage> stages = stageService.getAllStages();
		Map<String, List<DropInfo>> dropInfosMap =
				dropInfoService.getOpeningDropInfosMap(server, System.currentTimeMillis());
		Iterator<Stage> iter = stages.iterator();
		while (iter.hasNext()) {
			Stage stage = iter.next();
			List<DropInfo> infos = dropInfosMap.get(stage.getStageId());
			if (infos != null && !infos.isEmpty())
				stage.setDropInfos(infos);
			else
				iter.remove();
		}
		MappingJacksonValue result = new MappingJacksonValue(stages);
		result.setSerializationView(StageNewView.class);
		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("stageList").toString());
		return new ResponseEntity<MappingJacksonValue>(result, headers, HttpStatus.OK);
	}

	@ApiOperation("Get stage by stage ID")
	@GetMapping(path = "/{stageId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<MappingJacksonValue> getStageByStageId(
			@RequestParam(name = "server", required = false, defaultValue = "CN") Server server,
			@PathVariable("stageId") String stageId) {
		Stage stage = stageService.getStageByStageId(stageId);
		if (stage == null)
			return new ResponseEntity<MappingJacksonValue>(HttpStatus.NOT_FOUND);
		Map<String, List<DropInfo>> dropInfosMap =
				dropInfoService.getOpeningDropInfosMap(server, System.currentTimeMillis());
		stage.setDropInfos(dropInfosMap.get(stageId));
		MappingJacksonValue result = new MappingJacksonValue(stage);
		result.setSerializationView(StageNewView.class);
		return new ResponseEntity<MappingJacksonValue>(result, stage != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@GetMapping(path = "/cache")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'stageList'"),
			@CacheEvict(value = "maps", key = "'stageMap'"), @CacheEvict(value = "drop-info-list", allEntries = true),
			@CacheEvict(value = "latest-time-range-map", allEntries = true),
			@CacheEvict(value = "dropset", allEntries = true)})
	public ResponseEntity<String> evictStageCache() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
