package io.penguinstats.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.model.Limitation;
import io.penguinstats.service.LimitationService;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/limitations")
public class LimitationController {

	@Autowired
	private LimitationService limitationService;

	@ApiOperation("Get all real limitations")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Limitation>> getAllExtendedLimitations() {
		Map<String, Limitation> limitationsMap = limitationService.getExtendedLimitationMap();
		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("extendedLimitationMap").toString());
		return new ResponseEntity<List<Limitation>>(new ArrayList<>(limitationsMap.values()), headers, HttpStatus.OK);
	}

	@ApiOperation("Get limitation by stageId")
	@GetMapping(path = "/{stageId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Limitation> getExtendedLimitation(@PathVariable("stageId") String stageId) {
		Limitation limitation = limitationService.getExtendedLimitation(stageId);
		return new ResponseEntity<Limitation>(limitation, limitation != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@GetMapping(path = "/cache")
	@Caching(evict = {@CacheEvict(value = "maps", key = "'extendedLimitationMap'"),
			@CacheEvict(value = "limitation", allEntries = true)})
	public ResponseEntity<String> evictLimitationCache() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
