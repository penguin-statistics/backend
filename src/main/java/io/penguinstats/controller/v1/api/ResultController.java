package io.penguinstats.controller.v1.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant;
import io.penguinstats.constant.Constant.CustomHeader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("resultController_v1")
@RequestMapping("/api/result")
@Api(tags = {"@ Deprecated APIs"})
@Deprecated
public class ResultController {

	@ApiOperation("Get matrix")
	@GetMapping(path = "/matrix", produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getMatrix(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(CustomHeader.X_PENGUIN_UPGRAGE, Constant.API_V2);
		return new ResponseEntity<>("Penguin Stats api v1 is not supported any more. Please use v2 instead, thanks.",
				headers, HttpStatus.NOT_FOUND);
	}

}
