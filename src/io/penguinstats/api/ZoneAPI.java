package io.penguinstats.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import io.penguinstats.bean.Stage;
import io.penguinstats.bean.Zone;
import io.penguinstats.service.StageService;
import io.penguinstats.service.ZoneService;

@Path("/zone")
public class ZoneAPI {

	private static final ZoneService zoneService = ZoneService.getInstance();
	private static final StageService stageService = StageService.getInstance();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getZones() {
		List<Zone> zones = zoneService.getAllZones();
		return Response.ok(new JSONObject().put("zones", zones).toString()).build();
	}

	@GET
	@Path("/{zoneId}/stage")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStages(@PathParam("zoneId") String zoneId) {
		if (zoneId == null)
			return Response.status(Status.BAD_REQUEST).build();
		Zone zone = zoneService.getZone(zoneId);
		if (zone == null)
			return Response.status(404).build();
		Map<String, Stage> stageMap = stageService.getStageMap();
		List<Stage> stages = new ArrayList<>();
		for (String stageID : zone.getStages())
			stages.add(stageMap.get(stageID));
		return Response.ok(new JSONObject().put("stages", stages).toString()).build();
	}

}
