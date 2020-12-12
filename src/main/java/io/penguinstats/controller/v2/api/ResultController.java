package io.penguinstats.controller.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.controller.v2.request.AdvancedQueryRequest;
import io.penguinstats.controller.v2.response.AdvancedQueryResponse;
import io.penguinstats.controller.v2.response.MatrixQueryResponse;
import io.penguinstats.controller.v2.response.PatternQueryResponse;
import io.penguinstats.controller.v2.response.TrendQueryResponse;
import io.penguinstats.enums.Server;
import io.penguinstats.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController("resultController_v2")
@RequestMapping("/api/v2/result")
@Api(tags = {"Result"})
public class ResultController {

	@Autowired
	private ResultUtil resultUtil;

	@ApiOperation(value = "Get matrix result",
			notes = "Return the Result Matrix in the \"lastest accumulatable time ranges\". Detailed instructions can be found at: https://developer.penguin-stats.io/docs/api-v2-instruction/matrix-api")
	@GetMapping(path = "/matrix", produces = "application/json;charset=UTF-8")
	public ResponseEntity<MatrixQueryResponse> getMatrix(HttpServletRequest request,
			@ApiParam(value = "Whether to see personal drop matrix or not. Default to be false.",
					required = false) @RequestParam(name = "is_personal", required = false,
							defaultValue = "false") boolean isPersonal,
			@ApiParam(value = "Whether showing closed stages or not. Default to be false.",
					required = false) @RequestParam(name = "show_closed_zones", required = false,
							defaultValue = "false") boolean showClosedZones,
			@ApiParam(value = "Indicate which server you want to query. Default is CN.",
					required = false) @RequestParam(name = "server", required = false,
							defaultValue = "CN") Server server,
			@ApiParam(
					value = "Do filter on final result by stage. It should be a list of stageIds separated by commas.",
					required = false) @RequestParam(name = "stageFilter", required = false) String stageFilter,
			@ApiParam(value = "Do filter on final result by item. It should be a list of itemIds separated by commas.",
					required = false) @RequestParam(name = "itemFilter", required = false) String itemFilter)
			throws Exception {
		return resultUtil.getMatrixHelper(request, server, showClosedZones, stageFilter, itemFilter, isPersonal);
	}

	@ApiOperation(value = "Get the segmented Result Matrix for all Items and Stages",
			notes = "Return the segmented Matrix results of server `server`.")
	@GetMapping(path = "/trends", produces = "application/json;charset=UTF-8")
	public ResponseEntity<TrendQueryResponse>
			getAllSegmentedDropResults(@ApiParam(value = "Indicate which server you want to query. Default is CN.",
					required = false) @RequestParam(name = "server", required = false,
							defaultValue = "CN") Server server)
					throws Exception {
		return resultUtil.getTrendHelper(server);
	}

	@ApiOperation(value = "Get pattern result", notes = "Return the Pattern Result in the \"lastest time ranges\".")
	@GetMapping(path = "/pattern", produces = "application/json;charset=UTF-8")
	public ResponseEntity<PatternQueryResponse> getPattern(HttpServletRequest request,
			@ApiParam(value = "Whether to see personal pattern result or not. Default to be false.",
					required = false) @RequestParam(name = "is_personal", required = false,
							defaultValue = "false") boolean isPersonal,
			@ApiParam(value = "Indicate which server you want to query. Default is CN.",
					required = false) @RequestParam(name = "server", required = false,
							defaultValue = "CN") Server server)
			throws Exception {
		return resultUtil.getPatternHelper(request, server, isPersonal);
	}

	@ApiOperation(value = "Execute advanced queries",
			notes = "Execute advanced queries in a batch and return the query results in an array.")
	@PostMapping(path = "/advanced", produces = "application/json;charset=UTF-8")
	public ResponseEntity<AdvancedQueryResponse> executeAdvancedQueries(
			@Valid @RequestBody AdvancedQueryRequest advancedQueryRequest, HttpServletRequest request) {
		return resultUtil.getAdvancedResultHelper(advancedQueryRequest, request);
	}

}
