package io.penguinstats.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.Item;
import io.penguinstats.model.Stage;
import io.penguinstats.model.Zone;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.ItemService;
import io.penguinstats.service.StageService;
import io.penguinstats.service.ZoneService;
import io.penguinstats.util.CookieUtil;
import io.penguinstats.util.JSONUtil;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/result")
public class ResultController {

	private static Logger logger = LogManager.getLogger(ResultController.class);

	@Autowired
	private ZoneService zoneService;
	@Autowired
	private StageService stageService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemDropService itemDropService;
	@Autowired
	private CookieUtil cookieUtil;

	@ApiOperation("Get matrix")
	@GetMapping(path = "/matrix", produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getMatrix(HttpServletRequest request,
			@RequestParam(name = "is_personal", required = false, defaultValue = "false") boolean isPersonal,
			@RequestParam(name = "is_weighted", required = false, defaultValue = "false") boolean isWeighted,
			@RequestParam(name = "show_item_details", required = false, defaultValue = "false") boolean showItemDetails,
			@RequestParam(name = "show_stage_details", required = false,
					defaultValue = "false") boolean showStageDetails,
			@RequestParam(name = "show_closed_zones", required = false,
					defaultValue = "false") boolean showClosedZones) {
		logger.info("GET /matrix");
		try {
			String userID = isPersonal ? cookieUtil.readUserIDFromCookie(request) : null;
			Criteria criteria = isPersonal && userID != null ? Criteria.where("userID").is(userID) : null;
			List<DropMatrixElement> elements = itemDropService.generateDropMatrixElements(criteria, isWeighted);

			JSONObject obj = new JSONObject();
			JSONArray array = new JSONArray();
			Map<String, Zone> zoneMap = showClosedZones ? null : zoneService.getZoneMap();
			Map<String, Item> itemMap = !showItemDetails ? null : itemService.getItemMap();
			Map<String, Stage> stageMap = !showStageDetails && showClosedZones ? null : stageService.getStageMap();

			for (DropMatrixElement element : elements) {
				JSONObject subObj = JSONUtil.convertObjectToJSONObject(element);
				if (!showClosedZones) {
					Stage stage = stageMap.get(element.getStageId());
					Zone zone = zoneMap.get(stage.getZoneId());
					Long currentTime = System.currentTimeMillis();
					if (!zone.isInTimeRange(currentTime))
						continue;
				}
				if (showItemDetails)
					subObj.put("item", JSONUtil.convertObjectToJSONObject(itemMap.get(element.getItemId())));
				if (showStageDetails)
					subObj.put("stage", JSONUtil.convertObjectToJSONObject(stageMap.get(element.getStageId())));
				array.put(subObj);
			}
			obj.put("matrix", array);
			HttpHeaders headers = new HttpHeaders();
			headers.add("LAST-UPDATE-TIME",
					LastUpdateTimeUtil
							.getLastUpdateTime(
									isWeighted ? "weightedDropMatrixElements" : "notWeightedDropMatrixElements")
							.toString());
			return new ResponseEntity<>(obj.toString(), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in getMatrix", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
