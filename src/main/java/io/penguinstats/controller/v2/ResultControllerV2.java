package io.penguinstats.controller.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.MatrixResult;
import io.penguinstats.model.StageTrend;
import io.penguinstats.model.TrendDetail;
import io.penguinstats.model.TrendResult;
import io.penguinstats.service.DropInfoService;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.util.CookieUtil;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v2/result")
public class ResultControllerV2 {

	private static Logger logger = LogManager.getLogger(ResultControllerV2.class);

	@Autowired
	private ItemDropService itemDropService;
	@Autowired
	private DropInfoService dropInfoService;
	@Autowired
	private CookieUtil cookieUtil;

	@ApiOperation("Get matrix")
	@GetMapping(path = "/matrix", produces = "application/json;charset=UTF-8")
	public ResponseEntity<MatrixResult> getMatrix(HttpServletRequest request,
			@RequestParam(name = "is_personal", required = false, defaultValue = "false") boolean isPersonal,
			@RequestParam(name = "show_closed_zones", required = false, defaultValue = "false") boolean showClosedZones,
			@RequestParam(name = "server", required = false, defaultValue = "CN") Server server) {
		logger.info("GET /matrix");
		try {
			String userID = isPersonal ? cookieUtil.readUserIDFromCookie(request) : null;
			List<DropMatrixElement> elements = itemDropService.generateGlobalDropMatrixElements(server, userID);

			if (!showClosedZones) {
				Set<String> openingStages =
						showClosedZones ? null : dropInfoService.getOpeningStages(server, System.currentTimeMillis());
				Iterator<DropMatrixElement> iter = elements.iterator();
				while (iter.hasNext()) {
					DropMatrixElement element = iter.next();
					if (!openingStages.contains(element.getStageId()))
						iter.remove();
				}
			}
			MatrixResult result = new MatrixResult(elements);

			HttpHeaders headers = new HttpHeaders();
			if (userID == null)
				headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil
						.getLastUpdateTime(LastUpdateMapKeyName.MATRIX_RESULT + "_" + server).toString());
			return new ResponseEntity<MatrixResult>(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in getMatrix", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation("Get segmented drop data for all items in all stages")
	@GetMapping(path = "/trends", produces = "application/json;charset=UTF-8")
	public ResponseEntity<TrendResult> getAllSegmentedDropResults(
			@RequestParam(name = "interval_day", required = false, defaultValue = "1") int interval,
			@RequestParam(name = "range_day", required = false, defaultValue = "14") int range,
			@RequestParam(name = "server", required = false, defaultValue = "CN") Server server) {
		Map<String, Map<String, List<DropMatrixElement>>> map =
				itemDropService.generateSegmentedGlobalDropMatrixElementMap(server, interval, range);

		Map<String, StageTrend> stageTrendMap = new HashMap<>();
		map.forEach((stageId, subMap) -> {
			Long[] startTime = new Long[] {null};
			Map<String, TrendDetail> trendDetailMap = new HashMap<>();
			subMap.forEach((itemId, elements) -> {
				List<Integer> quantityList = new ArrayList<>(elements.size());
				List<Integer> timesList = new ArrayList<>(elements.size());
				elements.forEach(element -> {
					quantityList.add(element != null ? element.getQuantity() : 0);
					timesList.add(element != null ? element.getTimes() : 0);
					Long start = element.getStart();
					if (startTime[0] == null || start != null && start.compareTo(startTime[0]) < 0)
						startTime[0] = start;
				});
				TrendDetail trendDetail = new TrendDetail(timesList, quantityList);
				trendDetailMap.put(itemId, trendDetail);
			});
			StageTrend stageTrend = new StageTrend(startTime[0], trendDetailMap);
			stageTrendMap.put(stageId, stageTrend);
		});
		TrendResult result = new TrendResult(stageTrendMap);

		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME",
				LastUpdateTimeUtil
						.getLastUpdateTime(
								LastUpdateMapKeyName.TREND_RESULT + "_" + server + "_" + interval + "_" + range)
						.toString());
		return new ResponseEntity<TrendResult>(result, headers, HttpStatus.OK);
	}

}
