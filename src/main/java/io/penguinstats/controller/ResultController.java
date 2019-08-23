package io.penguinstats.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.model.DropMatrix;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.Item;
import io.penguinstats.model.Stage;
import io.penguinstats.model.Zone;
import io.penguinstats.service.DropMatrixService;
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
	private DropMatrixService dropMatrixService;
	@Autowired
	private ItemDropService itemDropService;
	@Autowired
	private CookieUtil cookieUtil;

	//TODO: This method will be deprecated after the release of frontend-v2.
	@GetMapping(path = "/stage/{stageId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getResultForOneStage(@PathVariable("stageId") String stageId) {
		if (stageId == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		JSONObject obj = generateReturnObjForOneStage(stageId, dropMatrixService.getAllElements());
		return new ResponseEntity<>(obj.toString(), HttpStatus.OK);
	}

	//TODO: This method will be deprecated after the release of frontend-v2.
	@PostMapping(path = "/stage/{stageId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getPersonalResultForOneStage(HttpServletRequest request,
			@RequestBody String requestBody, @PathVariable("stageId") String stageId) {
		try {
			JSONObject obj = new JSONObject(requestBody);
			String userID = cookieUtil.readUserIDFromCookie(request);
			logger.info("user " + userID + " POST /stage/" + stageId + "\n" + obj.toString());

			JSONObject stageTimesObj = obj.has("stageTimes") ? obj.getJSONObject("stageTimes") : new JSONObject();
			JSONObject dropMatrixObj = obj.has("dropMatrix") ? obj.getJSONObject("dropMatrix") : new JSONObject();
			List<DropMatrix> elements =
					getDropMatrixListFromStageTimesAndDropMatrixMapObj(stageTimesObj, dropMatrixObj, userID);
			JSONObject returnObj = generateReturnObjForOneStage(stageId, elements);
			return new ResponseEntity<>(returnObj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in getPersonalResultForOneStage", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//TODO: This method will be deprecated after the release of frontend-v2.
	@GetMapping(path = "/item/{itemId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getResultForOneItem(@PathVariable("itemId") String itemId) {
		if (itemId == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		JSONObject obj = generateReturnObjForOneItem(itemId, dropMatrixService.getAllElements());
		return new ResponseEntity<>(obj.toString(), HttpStatus.OK);
	}

	//TODO: This method will be deprecated after the release of frontend-v2.
	@PostMapping(path = "/item/{itemId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getPersonalResultForOneItem(HttpServletRequest request,
			@RequestBody String requestBody, @PathVariable("itemId") String itemId) {
		try {
			JSONObject obj = new JSONObject(requestBody);
			String userID = cookieUtil.readUserIDFromCookie(request);
			logger.info("user " + userID + " POST /item/" + itemId + "\n" + obj.toString());

			JSONObject stageTimesObj = obj.has("stageTimes") ? obj.getJSONObject("stageTimes") : new JSONObject();
			JSONObject dropMatrixObj = obj.has("dropMatrix") ? obj.getJSONObject("dropMatrix") : new JSONObject();
			List<DropMatrix> elements =
					getDropMatrixListFromStageTimesAndDropMatrixMapObj(stageTimesObj, dropMatrixObj, userID);

			JSONObject returnObj = generateReturnObjForOneItem(itemId, elements);
			return new ResponseEntity<>(returnObj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in getPersonalResultForOneItem", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

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

	@ApiOperation("Get segmented drop data for all items in all stages")
	@GetMapping(path = "/trends", produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> getAllSegmentedDropResults(
			@RequestParam(name = "interval", required = true, defaultValue = "86400000") long interval) {
		Map<String, Map<String, List<DropMatrixElement>>> map =
				itemDropService.generateDropMatrixElements(null, interval);

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
				}
				subObj.put(itemId, new JSONObject().put("times", timesArray).put("quantity", quantityArray));
			}
			obj.put("interval", interval);
			obj.put("startTime", itemDropService.getMinTimestamp(stageId));
			obj.put("results", subObj);
			returnObj.put(stageId, obj);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("segmentedDropMatrixElements").toString());
		return new ResponseEntity<>(new JSONObject().put("results", returnObj).toString(), headers, HttpStatus.OK);
	}

	//TODO: This method will be deprecated after the release of frontend-v2.
	private JSONObject generateReturnObjForOneStage(String stageId, List<DropMatrix> elements) {
		Map<String, Item> itemMap = itemService.getItemMap();
		Map<String, Stage> stageMap = stageService.getStageMap();
		JSONObject obj = new JSONObject();
		obj.put("stage", JSONUtil.convertObjectToJSONObject(stageMap.get(stageId)));
		JSONArray dropsArray = new JSONArray();
		for (DropMatrix dropMatrix : elements) {
			if (dropMatrix.getStageId().equals(stageId)) {
				JSONObject subObj =
						new JSONObject().put("quantity", dropMatrix.getQuantity()).put("times", dropMatrix.getTimes());
				Item item = itemMap.get(dropMatrix.getItemId());
				subObj.put("item", JSONUtil.convertObjectToJSONObject(item));
				dropsArray.put(subObj);
			}
		}
		obj.put("drops", dropsArray);
		return obj;
	}

	//TODO: This method will be deprecated after the release of frontend-v2.
	private JSONObject generateReturnObjForOneItem(String itemId, List<DropMatrix> elements) {
		Map<String, Item> itemMap = itemService.getItemMap();
		Map<String, Stage> stageMap = stageService.getStageMap();
		JSONObject obj = new JSONObject();
		obj.put("item", JSONUtil.convertObjectToJSONObject(itemMap.get(itemId)));
		JSONArray dropsArray = new JSONArray();
		for (DropMatrix dropMatrix : elements) {
			if (dropMatrix.getItemId().equals(itemId)) {
				JSONObject subObj =
						new JSONObject().put("quantity", dropMatrix.getQuantity()).put("times", dropMatrix.getTimes());
				Stage stage = stageMap.get(dropMatrix.getStageId());
				subObj.put("stage", JSONUtil.convertObjectToJSONObject(stage));
				dropsArray.put(subObj);
			}
		}
		obj.put("drops", dropsArray);
		return obj;
	}

	//TODO: This method will be deprecated after the release of frontend-v2.
	private List<DropMatrix> getDropMatrixListFromStageTimesAndDropMatrixMapObj(JSONObject stageTimesObj,
			JSONObject dropMatrixObj, String userID) {
		Criteria criteria = userID == null ? null : Criteria.where("userID").is(userID);
		Map<String, Map<String, DropMatrix>> matrixMapFromDB =
				userID != null ? itemDropService.generateDropMatrixMap(criteria) : new HashMap<>();
		Map<String, List<Double>> stageTimesMapFromDB =
				userID != null ? itemDropService.getStageTimesMap(criteria, false) : new HashMap<>();
		Map<String, Item> itemMap = itemService.getItemMap();

		// merge quantity
		for (String stageId : dropMatrixObj.keySet()) {
			JSONObject subObj = dropMatrixObj.getJSONObject(stageId);
			Map<String, DropMatrix> subMap = matrixMapFromDB.getOrDefault(stageId, new HashMap<>());
			for (String itemId : subObj.keySet()) {
				Integer quantity = subObj.getInt(itemId);
				if (subMap.containsKey(itemId)) {
					DropMatrix dm = subMap.get(itemId);
					dm.setQuantity(dm.getQuantity() + quantity);
				} else {
					// set times to 0 temporarily, will update later
					subMap.put(itemId, new DropMatrix(stageId, itemId, quantity, 0));
				}
				matrixMapFromDB.put(stageId, subMap);
			}
		}

		// merge stage times
		for (String stageId : matrixMapFromDB.keySet()) {
			List<Double> stageTimes = stageTimesMapFromDB.get(stageId);
			if (stageTimesObj.has(stageId)) {
				JSONArray stageTimesArray = stageTimesObj.getJSONArray(stageId);
				if (stageTimes == null) {
					stageTimes = new ArrayList<>();
					for (int i = 0; i < stageTimesArray.length(); i++) {
						stageTimes.add((double)stageTimesArray.getInt(i));
					}
				} else {
					if (stageTimes.size() < stageTimesArray.length()) {
						// something is wrong with stageTimesArray, skip this stage
						continue;
					}
					for (int i = 0; i < stageTimesArray.length(); i++) {
						stageTimes.set(i, stageTimes.get(i) + stageTimesArray.getInt(i));
					}
				}
			}
			stageTimesMapFromDB.put(stageId, stageTimes);
		}

		// update merged stage times into matrixMapFromDB
		for (String stageId : matrixMapFromDB.keySet()) {
			Map<String, DropMatrix> subMap = matrixMapFromDB.get(stageId);
			for (String itemId : subMap.keySet()) {
				Item item = itemMap.get(itemId);
				if (item == null)
					continue;
				Integer addTimePoint = item.getAddTimePoint();
				if (addTimePoint == null)
					addTimePoint = 0;
				Double times = stageTimesMapFromDB.get(stageId).get(addTimePoint);
				subMap.get(itemId).setTimes(times.intValue());
			}
		}

		// convert matrixMapFromDB into a list of matrix elements
		List<DropMatrix> elements = new ArrayList<>();
		for (String stageId : matrixMapFromDB.keySet()) {
			for (String itemId : matrixMapFromDB.get(stageId).keySet()) {
				elements.add(matrixMapFromDB.get(stageId).get(itemId));
			}
		}
		return elements;
	}

}
