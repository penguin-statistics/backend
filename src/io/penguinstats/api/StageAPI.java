package io.penguinstats.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllStages() {
		List<Stage> stages = stageService.getAllStages();
		List<Material> materials = materialService.getAllMaterials();
		Map<Integer, Material> materialMap = new HashMap<>();
		for (Material material : materials) {
			materialMap.put(material.getId(), material);
		}
		JSONArray stagesJSONArray = new JSONArray();
		for (Stage stage : stages) {
			JSONObject stageJSONObj = new JSONObject().put("stageID", stage.getId()).put("code", stage.getCode());
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
			stageJSONObj.put("normalDrop", normalDropJSONArray).put("specialDrop", specialDropJSONArray)
					.put("extraDrop", extraDropJSONArray);
			stagesJSONArray.put(stageJSONObj);
		}
		return Response.ok().entity(new JSONObject().put("stages", stagesJSONArray).toString()).build();
	}

}
