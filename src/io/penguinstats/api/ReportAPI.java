package io.penguinstats.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import io.penguinstats.bean.Drop;
import io.penguinstats.bean.ItemDrop;
import io.penguinstats.service.DropMatrixService;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.StageTimesService;
import io.penguinstats.util.APIUtil;

@Path("/report")
public class ReportAPI {

	private static final ItemDropService itemDropService = ItemDropService.getInstance();
	private static final StageTimesService stageTimesService = StageTimesService.getInstance();
	private static final DropMatrixService dropMatrixService = DropMatrixService.getInstance();
	private static Logger logger = LogManager.getLogger(ReportAPI.class);

	@POST
	public Response saveSingleReport(@Context HttpServletRequest request, InputStream requestBodyStream) {
		try {
			String jsonString = APIUtil.convertStreamToString(requestBodyStream);
			JSONObject obj = new JSONObject(jsonString);
			logger.info("POST /report\n" + obj.toString(2));
			String ip = getClientIp(request);
			int stageID = obj.getInt("stageID");
			String stageType = obj.getString("stageType");
			int furnitureNum = obj.getInt("furnitureNum");
			JSONArray dropsArray = obj.getJSONArray("drops");
			List<Drop> drops = new ArrayList<>();
			for (int i = 0; i < dropsArray.length(); i++) {
				JSONObject dropObj = dropsArray.getJSONObject(i);
				Drop drop = new Drop(dropObj.getInt("itemID"), dropObj.getInt("quantity"));
				drops.add(drop);
			}
			ItemDrop itemDrop =
					new ItemDrop(stageID, stageType, 1, drops, System.currentTimeMillis(), ip, furnitureNum);
			boolean result = itemDropService.saveItemDrop(itemDrop);
			stageTimesService.addStageTimes(stageID, stageType, 1);
			for (Drop drop : drops) {
				dropMatrixService.addDropMatrix(stageID, stageType, drop.getItemID(), drop.getQuantity());
			}
			if (furnitureNum != 0) {
				dropMatrixService.addDropMatrix(stageID, stageType, -1, furnitureNum);
			}
			if (result)
				return Response.ok().build();
			else
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} catch (Exception e) {
			logger.error("Error in saveSingleReport", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	private String getClientIp(HttpServletRequest request) {
		String remoteAddr = null;
		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}
		return remoteAddr;
	}

}
