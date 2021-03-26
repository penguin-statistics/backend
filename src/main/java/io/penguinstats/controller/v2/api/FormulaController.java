package io.penguinstats.controller.v2.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.util.exception.ServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("formulaController_v2")
@RequestMapping("/api/v2/formula")
@Api(tags = {"Formula"})
public class FormulaController {

    @ApiOperation(value = "Get all Formulas", notes = "Get synthesis conversion formulas.")
    @GetMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getFormula() {
        Resource resource = new ClassPathResource("json/formula.json");
        File sourceFile;
        try {
            sourceFile = resource.getFile();
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }

        try (FileReader fileReader = new FileReader(sourceFile);
                BufferedReader reader = new BufferedReader(fileReader)) {

            StringBuilder builder = new StringBuilder();
            String currentLine = reader.readLine();
            while (currentLine != null) {
                builder.append(currentLine).append("\n");
                currentLine = reader.readLine();
            }
            return new ResponseEntity<>(builder.toString(), HttpStatus.OK);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }
}
