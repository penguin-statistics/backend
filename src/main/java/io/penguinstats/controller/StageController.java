package io.penguinstats.controller;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
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

import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.Stage;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.StageService;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/stages")
public class StageController {

	@Autowired
	private StageService stageService;
	@Autowired
	private ItemDropService itemDropService;

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

	@GetMapping(path = "/cache")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'stageList'"),
			@CacheEvict(value = "maps", key = "'stageMap'")})
	public ResponseEntity<String> evictStageCache() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation("Get segmented drop data for all items in one stage")
	@GetMapping(path = "/{stageId}/trends", produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getSegmentedDropResults(@PathVariable("stageId") String stageId,
			@RequestParam(name = "interval", required = true, defaultValue = "86400000") long interval) {
		Long startTime = itemDropService.getMinTimestamp(stageId);
		Map<String, List<DropMatrixElement>> map =
				itemDropService.generateDropMatrixElements(null, interval, startTime, stageId, null);
		JSONObject obj = new JSONObject();
		JSONObject subObj = new JSONObject();
		for (String itemId : map.keySet())
			subObj.put(itemId, getSegmentedItemResultsArray(map.get(itemId)));
		obj.put("interval", interval);
		obj.put("startTime", startTime);
		obj.put("results", subObj);
		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME",
				LastUpdateTimeUtil.getLastUpdateTime("segmentedDropMatrixElements_" + stageId).toString());
		return new ResponseEntity<>(obj.toString(), headers, HttpStatus.OK);
	}

	@ApiOperation("Get segmented drop data for an item in one stage")
	@GetMapping(path = "/{stageId}/trends/items/{itemId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getSegmentedDropResultsByItemId(@PathVariable("stageId") String stageId,
			@RequestParam(name = "interval", required = true, defaultValue = "86400000") long interval,
			@RequestParam(name = "itemId", required = true) String itemId) {
		Long startTime = itemDropService.getMinTimestamp(stageId);
		Map<String, List<DropMatrixElement>> map =
				itemDropService.generateDropMatrixElements(null, interval, startTime, stageId, itemId);
		JSONObject obj = new JSONObject();
		JSONObject subObj = new JSONObject();
		if (map.containsKey(itemId))
			subObj.put(itemId, getSegmentedItemResultsArray(map.get(itemId)));
		obj.put("interval", interval);
		obj.put("startTime", startTime);
		obj.put("results", subObj);
		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil
				.getLastUpdateTime("segmentedDropMatrixElements_" + stageId + "_" + itemId).toString());
		return new ResponseEntity<>(obj.toString(), headers, HttpStatus.OK);
	}

	private JSONArray getSegmentedItemResultsArray(List<DropMatrixElement> elements) {
		JSONArray array = new JSONArray();
		for (DropMatrixElement element : elements) {
			JSONObject elementObj = new JSONObject();
			if (element != null)
				elementObj.put("quantity", element.getQuantity()).put("times", element.getTimes());
			array.put(elementObj);
		}
		return array;
	}

}
