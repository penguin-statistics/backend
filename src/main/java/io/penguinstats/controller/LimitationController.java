package io.penguinstats.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.model.Limitation;
import io.penguinstats.service.LimitationService;

@RestController
@RequestMapping("/api/limitations")
public class LimitationController {

	@Resource(name = "limitationService")
	private LimitationService limitationService;

	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Limitation>> getAllRealLimitations() {
		Map<String, Limitation> limitationsMap = limitationService.getRealLimitationMap();
		return new ResponseEntity<List<Limitation>>(new ArrayList<>(limitationsMap.values()), HttpStatus.OK);
	}

}
