package io.penguinstats.controller.v2.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.controller.v2.response.MatrixQueryResponse;
import io.penguinstats.controller.v2.response.PatternQueryResponse;
import io.penguinstats.controller.v2.response.TrendQueryResponse;
import io.penguinstats.enums.Server;
import io.penguinstats.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("privateController_v2")
@RequestMapping("/api/v2/_private")
@Api(tags = {"Private"}, hidden = true)
public class PrivateController {

	@Autowired
	private ResultUtil resultUtil;

	@ApiOperation(value = "Get matrix result",
			notes = "Return the Result Matrix in the \"lastest accumulatable time ranges\". This is for internal use.",
			hidden = true)
	@GetMapping(path = "/result/matrix/{server:CN|US|JP|KR}/{source:global|personal}",
			produces = "application/json;charset=UTF-8")
	public ResponseEntity<MatrixQueryResponse> getMatrix(HttpServletRequest request,
			@PathVariable("server") Server server, @PathVariable("source") String source) throws Exception {
		return resultUtil.getMatrixHelper(request, server, true, null, null, "personal".equals(source));
	}

	@ApiOperation(value = "Get the segmented Result Matrix for all Items and Stages",
			notes = "Return the segmented Matrix results of server `server`. This is for internal use.", hidden = true)
	@GetMapping(path = "/result/trend/{server:CN|US|JP|KR}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<TrendQueryResponse> getAllSegmentedDropResults(HttpServletRequest request,
			@PathVariable("server") Server server) throws Exception {
		return resultUtil.getTrendHelper(server);
	}

	@ApiOperation(value = "Get pattern result",
			notes = "Return the Pattern Result in the \"lastest time ranges\". This is for internal use.",
			hidden = true)
	@GetMapping(path = "/result/pattern/{server:CN|US|JP|KR}/{source:global|personal}",
			produces = "application/json;charset=UTF-8")
	public ResponseEntity<PatternQueryResponse> getPattern(HttpServletRequest request,
			@PathVariable("server") Server server, @PathVariable("source") String source) throws Exception {
		return resultUtil.getPatternHelper(request, server, "personal".equals(source));
	}

}
