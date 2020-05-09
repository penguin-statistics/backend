package io.penguinstats.controller.v2.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.model.Item;
import io.penguinstats.service.ItemService;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController("itemController_v2")
@RequestMapping("/api/v2/items")
public class ItemController {

	@Autowired
	private ItemService itemService;

	@ApiOperation("Get all items")
	@GetMapping(produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<Item>> getAllItems() {
		List<Item> items = itemService.getAllItems();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.LAST_MODIFIED,
				LastUpdateTimeUtil.getLastUpdateTime(LastUpdateMapKeyName.ITEM_LIST).toString());
		return new ResponseEntity<List<Item>>(items, headers, HttpStatus.OK);
	}

	@ApiOperation("Get item by item ID")
	@GetMapping(path = "/{itemId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Item> getItemByItemId(@PathVariable("itemId") String itemId) {
		Item item = itemService.getItemByItemId(itemId);
		if (item == null)
			return new ResponseEntity<Item>(HttpStatus.NOT_FOUND);
		return new ResponseEntity<Item>(item, HttpStatus.OK);
	}

}
