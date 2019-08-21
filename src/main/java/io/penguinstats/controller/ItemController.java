package io.penguinstats.controller;

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
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.model.Item;
import io.penguinstats.service.ItemService;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/items")
public class ItemController {

	@Autowired
	private ItemService itemService;

	@ApiOperation("Get all items")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Item>> getAllItems() {
		List<Item> items = itemService.getAllItems();
		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("itemList").toString());
		return new ResponseEntity<List<Item>>(items, headers, HttpStatus.OK);
	}

	@ApiOperation("Get item by item ID")
	@GetMapping(path = "/{itemId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Item> getItemByItemId(@PathVariable("itemId") String itemId) {
		Item item = itemService.getItemByItemId(itemId);
		return new ResponseEntity<Item>(item, item != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@GetMapping(path = "/cache")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'itemList'"), @CacheEvict(value = "maps", key = "'itemMap'")})
	public ResponseEntity<String> evictItemCache() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
