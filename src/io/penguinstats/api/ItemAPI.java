package io.penguinstats.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import io.penguinstats.bean.Item;
import io.penguinstats.service.ItemService;

@Path("/item")
public class ItemAPI {

	private static final ItemService itemService = ItemService.getInstance();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllItems() {
		List<Item> items = itemService.getAllItems();
		items.sort((a, b) -> {
			if (a.getSortId() == null && b.getSortId() == null)
				return a.getName().compareTo(b.getName());
			return a.getSortId() == null ? 1 : b.getSortId() == null ? -1 : a.getSortId() - b.getSortId();
		});
		JSONArray itemsJSONArray = new JSONArray();
		for (Item item : items) {
			itemsJSONArray.put(item.asJSON());
		}
		return Response.ok().entity(new JSONObject().put("items", itemsJSONArray).toString()).build();
	}

}
