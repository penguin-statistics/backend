package io.penguinstats.controller.v2.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.constant.Constant.SystemPropertyKey;
import io.penguinstats.controller.v2.mapper.QueryMapper;
import io.penguinstats.controller.v2.request.AdvancedQueryRequest;
import io.penguinstats.controller.v2.response.AdvancedQueryResponse;
import io.penguinstats.controller.v2.response.BasicQueryResponse;
import io.penguinstats.controller.v2.response.MatrixQueryResponse;
import io.penguinstats.controller.v2.response.TrendQueryResponse;
import io.penguinstats.enums.QueryType;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.query.BasicQuery;
import io.penguinstats.model.query.GlobalMatrixQuery;
import io.penguinstats.model.query.GlobalTrendQuery;
import io.penguinstats.model.query.QueryFactory;
import io.penguinstats.service.DropInfoService;
import io.penguinstats.service.SystemPropertyService;
import io.penguinstats.util.CookieUtil;
import io.penguinstats.util.DateUtil;
import io.penguinstats.util.DropMatrixElementUtil;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController("resultController_v2")
@RequestMapping("/api/v2/result")
@Api(tags = {"Result"})
public class ResultController {

	@Autowired
	private DropInfoService dropInfoService;
	@Autowired
	private SystemPropertyService systemPropertyService;
	@Autowired
	private CookieUtil cookieUtil;
	@Autowired
	private QueryMapper queryMapper;
	@Autowired
	private QueryFactory queryFactory;

	@ApiOperation(value = "Get the Result Matrix for all Stages and Items",
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
		log.info("GET /matrix");
		String userID = isPersonal ? cookieUtil.readUserIDFromCookie(request) : null;
		if (isPersonal && userID == null) {
			return new ResponseEntity<MatrixQueryResponse>(new MatrixQueryResponse(new ArrayList<>()), HttpStatus.OK);
		}

		GlobalMatrixQuery pastQuery = (GlobalMatrixQuery)queryFactory.getQuery(QueryType.GLOBAL_MATRIX);
		Integer pastTimeout =
				systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.PAST_GLOBAL_MATRIX_QUERY_TIMEOUT);
		pastQuery.setServer(server).setUserID(userID).setIsPast(true);
		if (pastTimeout != null)
			pastQuery.setTimeout(pastTimeout);
		List<DropMatrixElement> pastElements = pastQuery.execute();

		GlobalMatrixQuery currentQuery = (GlobalMatrixQuery)queryFactory.getQuery(QueryType.GLOBAL_MATRIX);
		Integer currentTimeout =
				systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.CURRENT_GLOBAL_MATRIX_QUERY_TIMEOUT);
		currentQuery.setServer(server).setUserID(userID).setIsPast(false);
		if (currentTimeout != null)
			currentQuery.setTimeout(currentTimeout);
		List<DropMatrixElement> currentElements = currentQuery.execute();

		List<DropMatrixElement> elements = DropMatrixElementUtil.combineElementLists(pastElements, currentElements);

		if (!showClosedZones)
			removeClosedStages(elements, server);

		if (stageFilter != null)
			filterStages(elements, stageFilter);

		if (itemFilter != null)
			filterItems(elements, itemFilter);

		MatrixQueryResponse result = new MatrixQueryResponse(elements);

		List<String> keyNames = Arrays.asList(LastUpdateMapKeyName.PAST_MATRIX_RESULT + "_" + server,
				LastUpdateMapKeyName.CURRENT_MATRIX_RESULT + "_" + server);
		Long lastUpdateTime = LastUpdateTimeUtil.findMaxLastUpdateTime(keyNames);

		HttpHeaders headers = new HttpHeaders();
		if (userID == null) {
			String lastModified = DateUtil.formatDate(new Date(lastUpdateTime));
			headers.add(HttpHeaders.LAST_MODIFIED, lastModified);
		}

		return new ResponseEntity<MatrixQueryResponse>(result, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Get the segmented Result Matrix for all Items and Stages",
			notes = "Return the segmented Matrix results of server `server` with granularity of "
					+ "`interval_day` days in the recent `range_day` days.")
	@GetMapping(path = "/trends", produces = "application/json;charset=UTF-8")
	public ResponseEntity<TrendQueryResponse> getAllSegmentedDropResults(
			@ApiParam(value = "The length of each section. Unit is \"millisecond\".",
					required = false) @RequestParam(name = "interval", required = false) Long interval,
			@ApiParam(
					value = "The total length of the time range used this query. The start time will be calculated using current time minus this value. Unit is \"millisecond\".",
					required = false) @RequestParam(name = "range", required = false) Long range,
			@ApiParam(value = "Indicate which server you want to query. Default is CN.",
					required = false) @RequestParam(name = "server", required = false,
							defaultValue = "CN") Server server)
			throws Exception {
		if (interval == null)
			interval = systemPropertyService.getPropertyLongValue(SystemPropertyKey.DEFAULT_GLOBAL_TREND_INTERVAL);
		if (range == null)
			range = systemPropertyService.getPropertyLongValue(SystemPropertyKey.DEFAULT_GLOBAL_TREND_RANGE);
		GlobalTrendQuery query = (GlobalTrendQuery)queryFactory.getQuery(QueryType.GLOBAL_TREND);
		Integer timeout = systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.GLOBAL_TREND_QUERY_TIMEOUT);
		query.setServer(server).setInterval(interval).setRange(range);
		if (timeout != null)
			query.setTimeout(timeout);
		List<DropMatrixElement> elements = query.execute();

		TrendQueryResponse result = new TrendQueryResponse(elements);

		HttpHeaders headers = generateLastModifiedHeadersFromLastUpdateMap(
				LastUpdateMapKeyName.TREND_RESULT + "_" + server + "_" + interval + "_" + range);

		return new ResponseEntity<TrendQueryResponse>(result, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Execute advanced queries",
			notes = "Execute advanced queries in a batch and return the query results in an array.")
	@PostMapping(path = "/advanced", produces = "application/json;charset=UTF-8")
	public ResponseEntity<AdvancedQueryResponse> executeAdvancedQueries(
			@Valid @RequestBody AdvancedQueryRequest advancedQueryRequest, HttpServletRequest request) {
		Integer maxQueryNum =
				systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.ADVANCED_QUERY_REQUEST_NUM_MAX);
		if (advancedQueryRequest.getQueries().size() > maxQueryNum) {
			AdvancedQueryResponse advancedQueryResponse =
					new AdvancedQueryResponse("Too many quiries. Max num is " + maxQueryNum);
			return new ResponseEntity<>(advancedQueryResponse, HttpStatus.BAD_REQUEST);
		}
		final String userIDFromCookie = cookieUtil.readUserIDFromCookie(request);
		List<BasicQueryResponse> results = new ArrayList<>();
		advancedQueryRequest.getQueries().forEach(singleQuery -> {
			try {
				Boolean isPersonal = Optional.ofNullable(singleQuery.getIsPersonal()).orElse(false);
				String userID = isPersonal ? userIDFromCookie : null;
				Integer timeout =
						systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.ADVANCED_QUERY_TIMEOUT);
				BasicQuery query = queryMapper.queryRequestToQueryModel(singleQuery, userID, timeout);
				List<DropMatrixElement> elements = query.execute();
				BasicQueryResponse queryResponse = queryMapper.elementsToBasicQueryResponse(singleQuery, elements);
				results.add(queryResponse);
			} catch (TimeoutException toEx) {
				log.error("TimeoutException in executeAdvancedQueries: ", toEx);
			} catch (ExecutionException exeEx) {
				log.error("ExecutionException in executeAdvancedQueries: ", exeEx);
			} catch (Exception ex) {
				log.error("Error in executeAdvancedQueries: ", ex);
			}
		});
		AdvancedQueryResponse advancedQueryResponse = new AdvancedQueryResponse(results);
		return new ResponseEntity<AdvancedQueryResponse>(advancedQueryResponse, HttpStatus.OK);
	}

	private void removeClosedStages(List<DropMatrixElement> elements, Server server) {
		Set<String> openingStages = dropInfoService.getOpeningStages(server, System.currentTimeMillis());
		Iterator<DropMatrixElement> iter = elements.iterator();
		while (iter.hasNext()) {
			DropMatrixElement element = iter.next();
			if (!openingStages.contains(element.getStageId()))
				iter.remove();
		}
	}

	private void filterStages(List<DropMatrixElement> elements, String stageFilter) {
		Set<String> filters = extractFilters(stageFilter);
		if (filters.isEmpty())
			return;
		Iterator<DropMatrixElement> iter = elements.iterator();
		while (iter.hasNext()) {
			DropMatrixElement element = iter.next();
			if (!filters.contains(element.getStageId()))
				iter.remove();
		}
	}

	private void filterItems(List<DropMatrixElement> elements, String itemFilter) {
		Set<String> filters = extractFilters(itemFilter);
		if (filters.isEmpty())
			return;
		Iterator<DropMatrixElement> iter = elements.iterator();
		while (iter.hasNext()) {
			DropMatrixElement element = iter.next();
			if (!filters.contains(element.getItemId()))
				iter.remove();
		}
	}

	private Set<String> extractFilters(String filterStr) {
		String[] splitted = filterStr.split(",");
		return Arrays.asList(splitted).stream().map(String::trim).collect(Collectors.toSet());
	}

	private HttpHeaders generateLastModifiedHeadersFromLastUpdateMap(String key) {
		String lastModified = DateUtil.formatDate(new Date(LastUpdateTimeUtil.getLastUpdateTime(key)));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.LAST_MODIFIED, lastModified);
		return headers;
	}

}
