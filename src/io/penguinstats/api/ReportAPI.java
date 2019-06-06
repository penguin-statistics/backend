package io.penguinstats.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import io.penguinstats.bean.Item;
import io.penguinstats.bean.ItemDrop;
import io.penguinstats.service.DropMatrixService;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.ItemService;
import io.penguinstats.util.APIUtil;

@Path("/report")
public class ReportAPI {

	private static final ItemDropService itemDropService = ItemDropService.getInstance();
	private static final DropMatrixService dropMatrixService = DropMatrixService.getInstance();
	private static final ItemService itemService = ItemService.getInstance();
	private static Logger logger = LogManager.getLogger(ReportAPI.class);

	@POST
	public Response saveSingleReport(@Context HttpServletRequest request, InputStream requestBodyStream) {
		try {
			String jsonString = APIUtil.convertStreamToString(requestBodyStream);
			JSONObject obj = new JSONObject(jsonString);
			logger.info("POST /report\n" + obj.toString(2));
			String ip = getClientIp(request);
			String stageId = obj.getString("stageId");
			int furnitureNum = obj.getInt("furnitureNum");
			JSONArray dropsArray = obj.getJSONArray("drops");
			List<Drop> drops = new ArrayList<>();
			for (int i = 0; i < dropsArray.length(); i++) {
				JSONObject dropObj = dropsArray.getJSONObject(i);
				Drop drop = new Drop(dropObj.getString("itemId"), dropObj.getInt("quantity"));
				drops.add(drop);
			}
			if (furnitureNum > 0)
				drops.add(new Drop("furni", furnitureNum));
			Boolean isReliable = checkDrops(drops) && (furnitureNum <= 1);
			if (!isReliable)
				logger.warn("Abnormal drop data!");
			Long timestamp = System.currentTimeMillis();
			ItemDrop itemDrop = new ItemDrop(stageId, 1, drops, timestamp, ip, isReliable);
			boolean result = itemDropService.saveItemDrop(itemDrop);
			if (isReliable) {
				for (Drop drop : drops)
					dropMatrixService.addDrop(stageId, drop.getItemId(), 1, drop.getQuantity());
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

	private boolean checkDrops(List<Drop> drops) {
		Map<String, Item> map = itemService.getItemMap();
		for (Drop drop : drops) {
			String itemId = drop.getItemId();
			if (!map.containsKey(itemId))
				return false;
			Item item = map.get(itemId);
			if (item.getItemType().equals("CARD_EXP") || item.getName().equals("赤金"))
				continue;
			int rarity = item.getRarity();
			int quantity = drop.getQuantity();
			if (rarity == 0 && quantity >= 5)
				return false;
			if (rarity == 1 && quantity >= 4)
				return false;
			if (rarity == 2 && quantity >= 3)
				return false;
			if (rarity == 3 && quantity >= 2)
				return false;
		}
		return true;
	}

}
