package io.penguinstats.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import io.penguinstats.bean.Material;
import io.penguinstats.service.MaterialService;

@Path("/item")
public class ItemAPI {

	private static final MaterialService materialService = MaterialService.getInstance();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllItems() {
		List<Material> materials = materialService.getAllMaterials();
		JSONArray itemsJSONArray = new JSONArray();
		for (Material material : materials) {
			itemsJSONArray.put(material.asJSON());
		}
		return Response.ok().entity(new JSONObject().put("items", itemsJSONArray).toString()).build();
	}

}
