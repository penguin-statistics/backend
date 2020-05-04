package io.penguinstats.controller.v2;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.enums.Server;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.Item;
import io.penguinstats.model.Stage;
import io.penguinstats.service.DropInfoService;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.ItemService;
import io.penguinstats.service.StageService;
import io.penguinstats.util.CookieUtil;
import io.penguinstats.util.JSONUtil;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v2/result")
public class ResultControllerV2 {

	private static Logger logger = LogManager.getLogger(ResultControllerV2.class);

	@Autowired
	private StageService stageService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemDropService itemDropService;
	@Autowired
	private DropInfoService dropInfoService;
	@Autowired
	private CookieUtil cookieUtil;

	@ApiOperation("Get matrix")
	@GetMapping(path = "/matrix", produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getMatrix(HttpServletRequest request,
			@RequestParam(name = "is_personal", required = false, defaultValue = "false") boolean isPersonal,
			@RequestParam(name = "show_item_details", required = false, defaultValue = "false") boolean showItemDetails,
			@RequestParam(name = "show_stage_details", required = false,
					defaultValue = "false") boolean showStageDetails,
			@RequestParam(name = "show_closed_zones", required = false, defaultValue = "false") boolean showClosedZones,
			@RequestParam(name = "server", required = false, defaultValue = "CN") Server server) {
		logger.info("GET /matrix");
		try {
			String userID = isPersonal ? cookieUtil.readUserIDFromCookie(request) : null;
			List<DropMatrixElement> elements = itemDropService.generateGlobalDropMatrixElements(server, userID);

			JSONObject obj = new JSONObject();
			JSONArray array = new JSONArray();
			Map<String, Item> itemMap = !showItemDetails ? null : itemService.getItemMap();
			Map<String, Stage> stageMap = !showStageDetails && showClosedZones ? null : stageService.getStageMap();
			Set<String> openingStages =
					showClosedZones ? null : dropInfoService.getOpeningStages(server, System.currentTimeMillis());

			for (DropMatrixElement element : elements) {
				JSONObject subObj = JSONUtil.convertObjectToJSONObject(element);
				if (!showClosedZones && !openingStages.contains(element.getStageId()))
					continue;
				if (showItemDetails)
					subObj.put("item", JSONUtil.convertObjectToJSONObject(itemMap.get(element.getItemId())));
				if (showStageDetails)
					subObj.put("stage", JSONUtil.convertObjectToJSONObject(stageMap.get(element.getStageId())));
				array.put(subObj);
			}
			obj.put("matrix", array);
			HttpHeaders headers = new HttpHeaders();
			headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("DropMatrixElements").toString());
			return new ResponseEntity<>(obj.toString(), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in getMatrix", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation("Get segmented drop data for all items in all stages")
	@GetMapping(path = "/trends", produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getAllSegmentedDropResults(
			@RequestParam(name = "interval_day", required = false, defaultValue = "1") int interval,
			@RequestParam(name = "range_day", required = false, defaultValue = "14") int range,
			@RequestParam(name = "server", required = false, defaultValue = "CN") Server server) {
		Map<String, Map<String, List<DropMatrixElement>>> map =
				itemDropService.generateSegmentedGlobalDropMatrixElementMap(server, interval, range);

		Long startTime = null;
		JSONObject returnObj = new JSONObject();
		for (String stageId : map.keySet()) {
			Map<String, List<DropMatrixElement>> subMap = map.get(stageId);
			JSONObject obj = new JSONObject();
			JSONObject subObj = new JSONObject();
			for (String itemId : subMap.keySet()) {
				List<DropMatrixElement> elements = subMap.get(itemId);
				JSONArray quantityArray = new JSONArray();
				JSONArray timesArray = new JSONArray();
				for (DropMatrixElement element : elements) {
					quantityArray.put(element != null ? element.getQuantity() : 0);
					timesArray.put(element != null ? element.getTimes() : 0);
					Long start = element.getStart();
					if (startTime == null || start != null && start.compareTo(startTime) < 0)
						startTime = start;
				}
				subObj.put(itemId, new JSONObject().put("times", timesArray).put("quantity", quantityArray));
			}
			obj.put("startTime", startTime);
			obj.put("results", subObj);
			returnObj.put(stageId, obj);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("segmentedDropMatrixElements").toString());
		return new ResponseEntity<>(new JSONObject().put("results", returnObj).toString(), headers, HttpStatus.OK);
	}

}
