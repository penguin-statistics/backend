package io.penguinstats.api;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import io.penguinstats.bean.Limitation;
import io.penguinstats.service.LimitationService;

@Path("/limitation")
public class LimitationAPI {

	private static final LimitationService limitationService = LimitationService.getInstance();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllRealLimitations() {
		Map<String, Limitation> limitationsMap = limitationService.getRealLimitationMap();
		JSONArray limitationsJSONArray = new JSONArray();
		for (String stageId : limitationsMap.keySet()) {
			Limitation limitation = limitationsMap.get(stageId);
			if (limitation != null)
				limitationsJSONArray.put(limitation.asJSON());
		}
		return Response.ok().entity(new JSONObject().put("limitations", limitationsJSONArray).toString()).build();
	}

}
