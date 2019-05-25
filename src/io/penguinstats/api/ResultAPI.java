package io.penguinstats.api;

import java.util.Map;

import javax.ws.rs.GET;
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
import io.penguinstats.service.MaterialService;
import io.penguinstats.service.StageService;
import io.penguinstats.service.StageTimesService;
import io.penguinstats.util.Tuple;

@Path("/result")
public class ResultAPI {

	private static final StageService stageService = StageService.getInstance();
	private static final MaterialService materialService = MaterialService.getInstance();
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
		logger.info("GET /stage/" + stageID + "/" + stageType);
		Map<Integer, Stage> stageMap = stageService.getStageMap();
		Map<Integer, Material> materialMap = materialService.getMaterialMap();
		Map<Tuple<Integer, String>, Integer> stageTimesMap = stageTimesService.getStageTimesMap();
		Map<Tuple<Integer, String>, Map<Integer, Integer>> dropMatrixMap = dropMatrixService.getDropMatrixMap();

		Tuple<Integer, String> stageTuple = new Tuple<>(stageID, stageType);
		Integer times = stageTimesMap.get(stageTuple);
		Map<Integer, Integer> subMap = dropMatrixMap.get(stageTuple);
		JSONObject obj = new JSONObject();
		obj.put("stage", stageMap.get(stageID).asJSON());
		obj.put("stageType", stageType);
		obj.put("times", times == null ? 0 : times);
		JSONArray dropsArray = new JSONArray();
		if (subMap != null) {
			for (Integer itemID : subMap.keySet()) {
				if (itemID != -1) {
					JSONObject itemObj = materialMap.get(itemID).asJSON();
					dropsArray.put(new JSONObject().put("item", itemObj).put("quantity", subMap.get(itemID)));
				} else {
					JSONObject furnitureObj = new JSONObject().put("id", itemID).put("name", "家具").put("rarity", -1)
							.put("itemType", "furniture");
					dropsArray.put(new JSONObject().put("item", furnitureObj).put("quantity", subMap.get(itemID)));
				}
			}
		}
		obj.put("drops", dropsArray);
		return Response.ok(obj.toString()).build();
	}

	@GET
	@Path("/item/{itemID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getResultForOneItem(@PathParam("itemID") Integer itemID) {
		if (itemID == null)
			return Response.status(Status.BAD_REQUEST).build();
		logger.info("GET /item/" + itemID);
		Map<Integer, Stage> stageMap = stageService.getStageMap();
		Map<Integer, Material> materialMap = materialService.getMaterialMap();
		Map<Tuple<Integer, String>, Integer> stageTimesMap = stageTimesService.getStageTimesMap();
		Map<Tuple<Integer, String>, Map<Integer, Integer>> dropMatrixMap = dropMatrixService.getDropMatrixMap();

		JSONObject obj = new JSONObject();
		if (itemID != -1) {
			obj.put("item", materialMap.get(itemID).asJSON());
		} else {
			JSONObject furnitureObj =
					new JSONObject().put("id", itemID).put("name", "家具").put("rarity", -1).put("itemType", "furniture");
			obj.put("item", furnitureObj);
		}

		JSONArray dropsArray = new JSONArray();
		for (Tuple<Integer, String> tuple : dropMatrixMap.keySet()) {
			Map<Integer, Integer> subMap = dropMatrixMap.get(tuple);
			if (subMap == null || !subMap.containsKey(itemID))
				continue;
			JSONObject dropObj = new JSONObject();
			dropObj.put("stage", stageMap.get(tuple.getX()).asJSON().put("stageType", tuple.getY()));
			dropObj.put("times", stageTimesMap.get(tuple));
			dropObj.put("quantity", subMap.get(itemID));
			dropsArray.put(dropObj);
		}
		obj.put("drops", dropsArray);
		return Response.ok(obj.toString()).build();
	}

}
