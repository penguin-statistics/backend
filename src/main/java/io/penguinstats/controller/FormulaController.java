package io.penguinstats.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/formula")
public class FormulaController {

	private static Logger logger = LogManager.getLogger(FormulaController.class);

	@ApiOperation("Get formula")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getFormula() {
		Resource resource = new ClassPathResource("json/formula.json");
		try {
			File sourceFile = resource.getFile();
			BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
			StringBuilder builder = new StringBuilder();
			String currentLine = reader.readLine();
			while (currentLine != null) {
				builder.append(currentLine).append("\n");
				currentLine = reader.readLine();
			}
			reader.close();
			return new ResponseEntity<>(builder.toString(), HttpStatus.OK);
		} catch (IOException e) {
			logger.error("Error in getFormula: ", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
