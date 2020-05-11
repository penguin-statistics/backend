package io.penguinstats.controller.v1.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant;
import io.penguinstats.constant.Constant.CustomHeader;
import io.penguinstats.model.Item;
import io.penguinstats.service.ItemService;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController("itemController_v1")
@RequestMapping("/api/items")
public class ItemController {

	@Autowired
	private ItemService itemService;

	@ApiOperation("Get all items")
	@GetMapping(produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<Item>>
			getAllItems(@RequestParam(name = "i18n", required = false, defaultValue = "false") boolean i18n) {
		List<Item> items = itemService.getAllItems();
		if (!i18n)
			items.forEach(item -> item.toNonI18nView());
		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("itemList").toString());
		headers.add(CustomHeader.X_PENGUIN_UPGRAGE, Constant.API_V2);
		return new ResponseEntity<List<Item>>(items, headers, HttpStatus.OK);
	}

	@ApiOperation("Get item by item ID")
	@GetMapping(path = "/{itemId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Item> getItemByItemId(@PathVariable("itemId") String itemId,
			@RequestParam(name = "i18n", required = false, defaultValue = "false") boolean i18n) {
		Item item = itemService.getItemByItemId(itemId);
		HttpHeaders headers = new HttpHeaders();
		headers.add(CustomHeader.X_PENGUIN_UPGRAGE, Constant.API_V2);
		if (item == null)
			return new ResponseEntity<Item>(headers, HttpStatus.NOT_FOUND);
		if (!i18n)
			item.toNonI18nView();
		return new ResponseEntity<Item>(item, headers, HttpStatus.OK);
	}

	@GetMapping(path = "/cache")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'itemList'"), @CacheEvict(value = "maps", key = "'itemMap'")})
	public ResponseEntity<String> evictItemCache() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
