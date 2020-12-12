package io.penguinstats.controller.v1.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant;
import io.penguinstats.constant.Constant.CustomHeader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("limitationController_v1")
@RequestMapping("/api/limitations")
@Api(tags = {"@ Deprecated APIs"})
@Deprecated
public class LimitationController {

	@ApiOperation("Get all real limitations")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getAllExtendedLimitations() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(CustomHeader.X_PENGUIN_UPGRAGE, Constant.API_V2);
		return new ResponseEntity<>("Penguin Stats api v1 is not supported any more. Please use v2 instead, thanks.",
				headers, HttpStatus.NOT_FOUND);
	}

	@ApiOperation("Get limitation by stageId")
	@GetMapping(path = "/{stageId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getExtendedLimitation(@PathVariable("stageId") String stageId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(CustomHeader.X_PENGUIN_UPGRAGE, Constant.API_V2);
		return new ResponseEntity<>("Penguin Stats api v1 is not supported any more. Please use v2 instead, thanks.",
				headers, HttpStatus.NOT_FOUND);
	}

}