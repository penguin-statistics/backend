package io.penguinstats.controller.v1.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant;
import io.penguinstats.constant.Constant.CustomHeader;
import io.swagger.annotations.ApiOperation;

@RestController("reportController_v1")
@RequestMapping("/api/report")
@Deprecated
public class ReportController {

	@ApiOperation(value = "Save single report", hide = true)
	@PostMapping
	public ResponseEntity<String> saveSingleReport(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(CustomHeader.X_PENGUIN_UPGRAGE, Constant.API_V2);
		return new ResponseEntity<>(headers, HttpStatus.GONE);
	}

}
