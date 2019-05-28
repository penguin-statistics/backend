package io.penguinstats.api;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import io.penguinstats.bean.Material;
import io.penguinstats.bean.Stage;
import io.penguinstats.service.MaterialService;
import io.penguinstats.service.StageService;

@Path("/stage")
public class StageAPI {

	private static final StageService stageService = StageService.getInstance();
	private static final MaterialService materialService = MaterialService.getInstance();

	@GET
	@Path("/{stageID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDetailedStage(@PathParam("stageID") Integer stageID) {
		if (stageID == null)
			return Response.status(Status.BAD_REQUEST).build();
		Map<Integer, Stage> stageMap = stageService.getStageMap();
		Map<Integer, Material> materialMap = materialService.getMaterialMap();
		Stage stage = stageMap.get(stageID);
		JSONObject stageJSONObj = new JSONObject().put("id", stage.getId()).put("code", stage.getCode())
				.put("category", stage.getCategory()).put("apCost", stage.getApCost());
		JSONArray normalDropJSONArray = new JSONArray();
		for (Integer itemID : stage.getNormalDrop()) {
			normalDropJSONArray.put(materialMap.get(itemID).asJSON());
		}
		JSONArray specialDropJSONArray = new JSONArray();
		for (Integer itemID : stage.getSpecialDrop()) {
			specialDropJSONArray.put(materialMap.get(itemID).asJSON());
		}
		JSONArray extraDropJSONArray = new JSONArray();
		for (Integer itemID : stage.getExtraDrop()) {
			extraDropJSONArray.put(materialMap.get(itemID).asJSON());
		}
		stageJSONObj.put("normalDrop", normalDropJSONArray).put("specialDrop", specialDropJSONArray).put("extraDrop",
				extraDropJSONArray);
		return Response.ok().entity(stageJSONObj.toString()).build();
	}

}
