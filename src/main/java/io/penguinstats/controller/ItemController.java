package io.penguinstats.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MappingJacksonValue>
			getAllItems(@RequestParam(name = "i18n", required = false, defaultValue = "false") boolean i18n) {
		List<Item> items = itemService.getAllItems();
		MappingJacksonValue result = new MappingJacksonValue(items);
		result.setSerializationView(i18n ? Item.ItemI18nView.class : Item.ItemBaseView.class);
		HttpHeaders headers = new HttpHeaders();
		headers.add("LAST-UPDATE-TIME", LastUpdateTimeUtil.getLastUpdateTime("itemList").toString());
		return new ResponseEntity<MappingJacksonValue>(result, headers, HttpStatus.OK);
	}

	@ApiOperation("Get item by item ID")
	@GetMapping(path = "/{itemId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<MappingJacksonValue> getItemByItemId(@PathVariable("itemId") String itemId,
			@RequestParam(name = "i18n", required = false, defaultValue = "false") boolean i18n) {
		Item item = itemService.getItemByItemId(itemId);
		if (item == null)
			return new ResponseEntity<MappingJacksonValue>(HttpStatus.NOT_FOUND);

		MappingJacksonValue result = new MappingJacksonValue(item);
		result.setSerializationView(i18n ? Item.ItemI18nView.class : Item.ItemBaseView.class);
		return new ResponseEntity<MappingJacksonValue>(result, HttpStatus.OK);
	}

	@GetMapping(path = "/cache")
	@Caching(evict = {@CacheEvict(value = "lists", key = "'itemList'"), @CacheEvict(value = "maps", key = "'itemMap'")})
	public ResponseEntity<String> evictItemCache() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
