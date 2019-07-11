package io.penguinstats.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.penguinstats.api.filter.UserRelated;
import io.penguinstats.bean.Drop;
import io.penguinstats.bean.ItemDrop;
import io.penguinstats.service.DropMatrixService;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.util.APIUtil;
import io.penguinstats.util.LimitationUtil;

@Path("/report")
public class ReportAPI {

	private static final ItemDropService itemDropService = ItemDropService.getInstance();
	private static final DropMatrixService dropMatrixService = DropMatrixService.getInstance();

	private static Logger logger = LogManager.getLogger(ReportAPI.class);

	@POST
	@UserRelated
	public Response saveSingleReport(@Context HttpServletRequest request, InputStream requestBodyStream) {
		try {
			String jsonString = APIUtil.convertStreamToString(requestBodyStream);
			if (!isValidSingleReportRequest(jsonString)) {
				logger.warn("POST /report\n" + jsonString);
				return Response.status(Status.BAD_REQUEST).build();
			}
			JSONObject obj = new JSONObject(jsonString);
			String userID = getUserIDFromRequest(request);
			logger.info("user " + userID + " POST /report\n" + obj.toString(2));
			String ip = getClientIp(request);
			String stageId = obj.getString("stageId");
			int furnitureNum = obj.getInt("furnitureNum");
			JSONArray dropsArray = obj.getJSONArray("drops");
			String source = obj.has("source") ? obj.getString("source") : null;
			String version = obj.has("version") ? obj.getString("version") : null;
			List<Drop> drops = new ArrayList<>();
			for (int i = 0; i < dropsArray.length(); i++) {
				JSONObject dropObj = dropsArray.getJSONObject(i);
				Drop drop = new Drop(dropObj.getString("itemId"), dropObj.getInt("quantity"));
				drops.add(drop);
			}
			if (furnitureNum > 0)
				drops.add(new Drop("furni", furnitureNum));
			Boolean isReliable = LimitationUtil.checkDrops(drops, stageId);
			if (!isReliable)
				logger.warn("Abnormal drop data!");
			Long timestamp = System.currentTimeMillis();
			ItemDrop itemDrop = new ItemDrop(stageId, 1, drops, timestamp, ip, isReliable, source, version);
			boolean result = itemDropService.saveItemDrop(itemDrop);
			if (isReliable) {
				if (!dropMatrixService.hasElementsForOneStage(stageId))
					dropMatrixService.initializeElementsForOneStage(stageId);
				for (Drop drop : drops)
					dropMatrixService.increateQuantityForOneElement(stageId, drop.getItemId(), drop.getQuantity());
				dropMatrixService.increateTimesForOneStage(stageId, 1);
			}
			if (result)
				return Response.ok().build();
			else
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} catch (JSONException jsonException) {
			logger.error("Error in saveSingleReport", jsonException);
			return Response.status(Status.BAD_REQUEST).build();
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

	private boolean isValidSingleReportRequest(String jsonString) {
		try {
			JSONObject obj = new JSONObject(jsonString);
			if (!hasValidValue(obj, "stageId") || !hasValidValue(obj, "furnitureNum") || !hasValidValue(obj, "drops")) {
				return false;
			}
		} catch (JSONException e) {
			logger.error("Invalid single report request", e);
			return false;
		}
		return true;
	}

	private boolean hasValidValue(JSONObject obj, String key) {
		return obj.has(key) && !obj.isNull(key);
	}

	private String getUserIDFromRequest(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null)
			return null;
		return (String)session.getAttribute("userID");
	}

}
