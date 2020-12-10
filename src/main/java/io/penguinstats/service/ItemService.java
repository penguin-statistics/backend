package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.model.Item;

public interface ItemService {

	@Caching(evict = {@CacheEvict(value = "lists", key = "'itemList'"), @CacheEvict(value = "maps", key = "'itemMap'")})
	void saveItem(Item item);

	Item getItemByItemId(String itemId);

	@Cacheable(value = "lists", key = "'itemList'")
	List<Item> getAllItems();

	@Cacheable(value = "maps", key = "'itemMap'")
	Map<String, Item> getItemMap();

	@Cacheable(value = "maps", key = "'allNameItemMap'")
	Map<String, Item> getAllNameItemMap();

}
