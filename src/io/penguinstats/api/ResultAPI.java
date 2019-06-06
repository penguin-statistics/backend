package io.penguinstats.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import io.penguinstats.bean.DropMatrix;
import io.penguinstats.bean.Item;
import io.penguinstats.bean.Stage;
import io.penguinstats.service.DropMatrixService;
import io.penguinstats.service.ItemService;
import io.penguinstats.service.StageService;
import io.penguinstats.util.APIUtil;

@Path("/result")
public class ResultAPI {

	private static final StageService stageService = StageService.getInstance();
	private static final ItemService itemService = ItemService.getInstance();
	private static final DropMatrixService dropMatrixService = DropMatrixService.getInstance();
	private static Logger logger = LogManager.getLogger(ResultAPI.class);

	@GET
	@Path("/stage/{stageId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getResultForOneStage(@PathParam("stageId") String stageId) {
		if (stageId == null)
			return Response.status(Status.BAD_REQUEST).build();
		JSONObject obj = generateReturnObjForOneStage(stageId, dropMatrixService.getAllElements());
		return Response.ok(obj.toString()).build();
	}

	@POST
	@Path("/stage/{stageId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPersonalResultForOneStage(InputStream requestBodyStream, @PathParam("stageId") String stageId) {
		try {
			String jsonString = APIUtil.convertStreamToString(requestBodyStream);
			JSONObject obj = new JSONObject(jsonString);
			logger.info("POST /stage/" + stageId + "\n" + obj.toString());

			JSONObject stageTimesObj = obj.getJSONObject("stageTimes");
			JSONObject dropMatrixObj = obj.getJSONObject("dropMatrix");
			List<DropMatrix> elements =
					getDropMatrixListFromStageTimesAndDropMatrixMapObj(stageTimesObj, dropMatrixObj);
			JSONObject returnObj = generateReturnObjForOneStage(stageId, elements);
			return Response.ok(returnObj.toString()).build();
		} catch (Exception e) {
			logger.error("Error in saveSingleReport", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path("/item/{itemId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getResultForOneItem(@PathParam("itemId") String itemId) {
		if (itemId == null)
			return Response.status(Status.BAD_REQUEST).build();
		JSONObject obj = generateReturnObjForOneItem(itemId, dropMatrixService.getAllElements());
		return Response.ok(obj.toString()).build();
	}

	@POST
	@Path("/item/{itemId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPersonalResultForOneItem(InputStream requestBodyStream, @PathParam("itemId") String itemId) {
		try {
			String jsonString = APIUtil.convertStreamToString(requestBodyStream);
			JSONObject obj = new JSONObject(jsonString);
			logger.info("POST /item/" + itemId + "\n" + obj.toString());
			JSONObject stageTimesObj = obj.getJSONObject("stageTimes");
			JSONObject dropMatrixObj = obj.getJSONObject("dropMatrix");
			List<DropMatrix> elements =
					getDropMatrixListFromStageTimesAndDropMatrixMapObj(stageTimesObj, dropMatrixObj);

			JSONObject returnObj = generateReturnObjForOneItem(itemId, elements);
			return Response.ok(returnObj.toString()).build();
		} catch (Exception e) {
			logger.error("Error in saveSingleReport", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path("/matrix")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMatrix() {
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		List<DropMatrix> elements = dropMatrixService.getAllElements();
		for (DropMatrix element : elements) {
			array.put(element.asJSON());
		}
		obj.put("matrix", array);
		return Response.ok(obj.toString()).build();
	}

	private JSONObject generateReturnObjForOneStage(String stageId, List<DropMatrix> elements) {
		Map<String, Item> itemMap = itemService.getItemMap();
		Map<String, Stage> stageMap = stageService.getStageMap();
		JSONObject obj = new JSONObject();
		obj.put("stage", stageMap.get(stageId).asJSON());
		JSONArray dropsArray = new JSONArray();
		for (DropMatrix dropMatrix : elements) {
			JSONObject subObj =
					new JSONObject().put("quantity", dropMatrix.getQuantity()).put("times", dropMatrix.getTimes());
			Item item = itemMap.get(dropMatrix.getItemId());
			subObj.put("item", item.asJSON());
			dropsArray.put(subObj);
		}
		obj.put("drops", dropsArray);
		return obj;
	}

	private JSONObject generateReturnObjForOneItem(String itemId, List<DropMatrix> elements) {
		Map<String, Item> itemMap = itemService.getItemMap();
		Map<String, Stage> stageMap = stageService.getStageMap();
		JSONObject obj = new JSONObject();
		obj.put("item", itemMap.get(itemId).asJSON());
		JSONArray dropsArray = new JSONArray();
		for (DropMatrix dropMatrix : elements) {
			JSONObject subObj =
					new JSONObject().put("quantity", dropMatrix.getQuantity()).put("times", dropMatrix.getTimes());
			Stage stage = stageMap.get(dropMatrix.getStageId());
			subObj.put("stage", stage.asJSON());
			dropsArray.put(subObj);
		}
		obj.put("drops", dropsArray);
		return obj;
	}

	private List<DropMatrix> getDropMatrixListFromStageTimesAndDropMatrixMapObj(JSONObject stageTimesObj,
			JSONObject dropMatrixObj) {
		List<DropMatrix> elements = new ArrayList<>();
		Map<String, Item> itemMap = itemService.getItemMap();
		for (String stageId : dropMatrixObj.keySet()) {
			JSONObject subObj = dropMatrixObj.getJSONObject(stageId);
			JSONArray stageTimesArray = stageTimesObj.getJSONArray(stageId);
			for (String itemId : subObj.keySet()) {
				Integer quantity = subObj.getInt(itemId);
				Item item = itemMap.get(itemId);
				Integer addTimePoint = item.getAddTimePoint();
				if (addTimePoint == null)
					addTimePoint = 0;
				Integer times = stageTimesArray.getInt(addTimePoint);
				DropMatrix element = new DropMatrix(stageId, itemId, quantity, times);
				elements.add(element);
			}
		}
		return elements;
	}

}
