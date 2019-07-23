package io.penguinstats.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.model.Item;
import io.penguinstats.service.ItemService;

@RestController
@RequestMapping("/api/items")
public class ItemController {

	@Resource(name = "itemService")
	private ItemService itemService;

	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Item>> getAllItems() {
		return new ResponseEntity<List<Item>>(itemService.getAllItems(), HttpStatus.OK);
	}

	@GetMapping(path = "/{itemId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Item> getItemByItemId(@PathVariable("itemId") String itemId) {
		Item item = itemService.getItemByItemId(itemId);
		return new ResponseEntity<Item>(item, item != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

}
