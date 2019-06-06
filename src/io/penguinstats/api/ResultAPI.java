package io.penguinstats.api;

import java.io.InputStream;
import java.util.HashMap;
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

import io.penguinstats.bean.Material;
import io.penguinstats.bean.Stage;
import io.penguinstats.service.DropMatrixService;
import io.penguinstats.service.ItemService;
import io.penguinstats.service.StageService;
import io.penguinstats.service.StageTimesService;
import io.penguinstats.util.APIUtil;
import io.penguinstats.util.Tuple;

@Path("/result")
public class ResultAPI {

	private static final StageService stageService = StageService.getInstance();
	private static final ItemService materialService = ItemService.getInstance();
	private static final StageTimesService stageTimesService = StageTimesService.getInstance();
	private static final DropMatrixService dropMatrixService = DropMatrixService.getInstance();
	private static Logger logger = LogManager.getLogger(ResultAPI.class);

	@GET
	@Path("/stage/{stageID}/{stageType}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getResultForOneStage(@PathParam("stageID") Integer stageID,
			@PathParam("stageType") String stageType) {
		if (stageID == null || stageType == null)
			return Response.status(Status.BAD_REQUEST).build();

		Map<Tuple<Integer, String>, Map<Integer, Integer>> stageTimesMap = stageTimesService.getStageTimesMap();
		Map<Tuple<Integer, String>, Map<Integer, Integer>> dropMatrixMap = dropMatrixService.getDropMatrixMap();

		JSONObject obj = generateReturnObjForOneStage(stageID, stageType, stageTimesMap, dropMatrixMap);
		return Response.ok(obj.toString()).build();
	}

	@POST
	@Path("/stage/{stageID}/{stageType}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPersonalResultForOneStage(InputStream requestBodyStream, @PathParam("stageID") Integer stageID,
			@PathParam("stageType") String stageType) {
		try {
			String jsonString = APIUtil.convertStreamToString(requestBodyStream);
			JSONObject obj = new JSONObject(jsonString);
			logger.info("POST /stage/" + stageID + "/" + stageType + "\n" + obj.toString());

			JSONObject stageTimesObj = obj.getJSONObject("stageTimes");
			Map<Tuple<Integer, String>, Map<Integer, Integer>> stageTimesMap = getStageTimesMapFromObj(stageTimesObj);

			JSONObject dropMatrixObj = obj.getJSONObject("dropMatrix");
			Map<Tuple<Integer, String>, Map<Integer, Integer>> dropMatrixMap = getDropMatrixMapFromObj(dropMatrixObj);

			JSONObject returnObj = generateReturnObjForOneStage(stageID, stageType, stageTimesMap, dropMatrixMap);
			return Response.ok(returnObj.toString()).build();
		} catch (Exception e) {
			logger.error("Error in saveSingleReport", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path("/item/{itemID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getResultForOneItem(@PathParam("itemID") Integer itemID) {
		if (itemID == null)
			return Response.status(Status.BAD_REQUEST).build();
		Map<Tuple<Integer, String>, Map<Integer, Integer>> stageTimesMap = stageTimesService.getStageTimesMap();
		Map<Tuple<Integer, String>, Map<Integer, Integer>> dropMatrixMap = dropMatrixService.getDropMatrixMap();

		JSONObject obj = generateReturnObjForOneItem(itemID, stageTimesMap, dropMatrixMap);

		return Response.ok(obj.toString()).build();
	}

	@POST
	@Path("/item/{itemID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPersonalResultForOneItem(InputStream requestBodyStream, @PathParam("itemID") Integer itemID) {
		try {
			String jsonString = APIUtil.convertStreamToString(requestBodyStream);
			JSONObject obj = new JSONObject(jsonString);
			logger.info("POST /item/" + itemID + "\n" + obj.toString());
			JSONObject stageTimesObj = obj.getJSONObject("stageTimes");
			Map<Tuple<Integer, String>, Map<Integer, Integer>> stageTimesMap = getStageTimesMapFromObj(stageTimesObj);

			JSONObject dropMatrixObj = obj.getJSONObject("dropMatrix");
			Map<Tuple<Integer, String>, Map<Integer, Integer>> dropMatrixMap = getDropMatrixMapFromObj(dropMatrixObj);

			JSONObject returnObj = generateReturnObjForOneItem(itemID, stageTimesMap, dropMatrixMap);
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
		Map<Tuple<Integer, String>, Map<Integer, Integer>> stageTimesMap = stageTimesService.getStageTimesMap();
		Map<Tuple<Integer, String>, Map<Integer, Integer>> dropMatrixMap = dropMatrixService.getDropMatrixMap();
		Map<Integer, Material> materialMap = materialService.getMaterialMap();
		Map<Integer, Stage> stageMap = stageService.getStageMap();
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		for (Tuple<Integer, String> tuple : dropMatrixMap.keySet()) {
			int stageID = tuple.getX();
			Map<Integer, Integer> stageTimes = stageTimesMap.get(tuple);
			Map<Integer, Integer> dropMatrix = dropMatrixMap.get(tuple);
			for (Integer itemID : dropMatrix.keySet()) {
				JSONObject subObj = new JSONObject();
				subObj.put("stageID", stageID).put("itemID", itemID).put("quantity", dropMatrix.get(itemID))
						.put("times", stageTimes.get(materialMap.get(itemID).getTimePoint()))
						.put("apCost", stageMap.get(stageID).getApCost())
						.put("stageCode", stageMap.get(stageID).getCode())
						.put("itemName", materialMap.get(itemID).getName());
				array.put(subObj);
			}
		}
		obj.put("matrix", array);
		return Response.ok(obj.toString()).build();
	}

	private JSONObject generateReturnObjForOneStage(int stageID, String stageType,
			Map<Tuple<Integer, String>, Map<Integer, Integer>> stageTimesMap,
			Map<Tuple<Integer, String>, Map<Integer, Integer>> dropMatrixMap) {
		Map<Integer, Stage> stageMap = stageService.getStageMap();
		Map<Integer, Material> materialMap = materialService.getMaterialMap();
		Tuple<Integer, String> stageTuple = new Tuple<>(stageID, stageType);
		Map<Integer, Integer> timesMap = stageTimesMap.get(stageTuple);
		Map<Integer, Integer> subMap = dropMatrixMap.get(stageTuple);
		JSONObject obj = new JSONObject();
		obj.put("stage", stageMap.get(stageID).asJSON());
		obj.put("stageType", stageType);
		JSONArray dropsArray = new JSONArray();
		if (subMap != null) {
			for (Integer itemID : subMap.keySet()) {
				JSONObject subObj = new JSONObject().put("quantity", subMap.get(itemID));
				Material material = materialMap.get(itemID);
				subObj.put("item", material.asJSON());
				subObj.put("times", timesMap.get(material.getTimePoint()));
				dropsArray.put(subObj);
			}
		}
		obj.put("drops", dropsArray);
		return obj;
	}

	private JSONObject generateReturnObjForOneItem(int itemID,
			Map<Tuple<Integer, String>, Map<Integer, Integer>> stageTimesMap,
			Map<Tuple<Integer, String>, Map<Integer, Integer>> dropMatrixMap) {
		Map<Integer, Stage> stageMap = stageService.getStageMap();
		Map<Integer, Material> materialMap = materialService.getMaterialMap();
		JSONObject obj = new JSONObject();
		Material material = materialMap.get(itemID);
		obj.put("item", material.asJSON());

		JSONArray dropsArray = new JSONArray();
		for (Tuple<Integer, String> tuple : dropMatrixMap.keySet()) {
			Map<Integer, Integer> subMap = dropMatrixMap.get(tuple);
			if (subMap == null || !subMap.containsKey(itemID))
				continue;
			JSONObject dropObj = new JSONObject();
			dropObj.put("stage", stageMap.get(tuple.getX()).asJSON().put("stageType", tuple.getY()));
			dropObj.put("times", stageTimesMap.get(tuple).get(material.getTimePoint()));
			dropObj.put("quantity", subMap.get(itemID));
			dropsArray.put(dropObj);
		}
		obj.put("drops", dropsArray);
		return obj;
	}

	private Map<Tuple<Integer, String>, Map<Integer, Integer>> getStageTimesMapFromObj(JSONObject stageTimesObj) {
		Map<Tuple<Integer, String>, Map<Integer, Integer>> stageTimesMap = new HashMap<>();
		for (String oneStageIDStr : stageTimesObj.keySet()) {
			Tuple<Integer, String> tuple = new Tuple<>(Integer.valueOf(oneStageIDStr), "normal");
			JSONArray stageTimesArray = stageTimesObj.getJSONArray(oneStageIDStr);
			Map<Integer, Integer> subMap = new HashMap<>();
			for (int i = 0; i < stageTimesArray.length(); i++) {
				subMap.put(i, stageTimesArray.getInt(i));
			}
			stageTimesMap.put(tuple, subMap);
		}
		return stageTimesMap;
	}

	private Map<Tuple<Integer, String>, Map<Integer, Integer>> getDropMatrixMapFromObj(JSONObject dropMatrixObj) {
		Map<Tuple<Integer, String>, Map<Integer, Integer>> dropMatrixMap = new HashMap<>();
		for (String oneStageIDStr : dropMatrixObj.keySet()) {
			Tuple<Integer, String> tuple = new Tuple<>(Integer.valueOf(oneStageIDStr), "normal");
			JSONObject subObj = dropMatrixObj.getJSONObject(oneStageIDStr);
			Map<Integer, Integer> subMap = new HashMap<>();
			for (String itemIDStr : subObj.keySet()) {
				subMap.put(Integer.valueOf(itemIDStr), subObj.getInt(itemIDStr));
			}
			dropMatrixMap.put(tuple, subMap);
		}
		return dropMatrixMap;
	}

}
