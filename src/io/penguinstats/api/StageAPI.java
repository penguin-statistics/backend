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

import io.penguinstats.bean.Item;
import io.penguinstats.bean.Stage;
import io.penguinstats.service.ItemService;
import io.penguinstats.service.StageService;

@Path("/stage")
public class StageAPI {

	private static final StageService stageService = StageService.getInstance();
	private static final ItemService itemService = ItemService.getInstance();

	@GET
	@Path("/{stageId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDetailedStage(@PathParam("stageId") String stageId) {
		if (stageId == null)
			return Response.status(Status.BAD_REQUEST).build();
		Map<String, Stage> stageMap = stageService.getStageMap();
		Map<String, Item> itemMap = itemService.getItemMap();
		Stage stage = stageMap.get(stageId);
		JSONObject stageJSONObj = stage.asJSON();
		JSONArray normalDropJSONArray = new JSONArray();
		for (String itemId : stage.getNormalDrop()) {
			normalDropJSONArray.put(itemMap.get(itemId).asJSON());
		}
		JSONArray specialDropJSONArray = new JSONArray();
		for (String itemId : stage.getSpecialDrop()) {
			specialDropJSONArray.put(itemMap.get(itemId).asJSON());
		}
		JSONArray extraDropJSONArray = new JSONArray();
		for (String itemId : stage.getExtraDrop()) {
			extraDropJSONArray.put(itemMap.get(itemId).asJSON());
		}
		stageJSONObj.put("normalDrop", normalDropJSONArray).put("specialDrop", specialDropJSONArray).put("extraDrop",
				extraDropJSONArray);
		return Response.ok().entity(stageJSONObj.toString()).build();
	}

}
