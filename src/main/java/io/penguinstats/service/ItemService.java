package io.penguinstats.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.constant.Constant.CacheValue;
import io.penguinstats.model.Item;

public interface ItemService {

    @Caching(evict = {@CacheEvict(value = CacheValue.LISTS, key = "'itemList'"),
            @CacheEvict(value = CacheValue.MAPS, key = "'itemMap'")})
    void saveItem(Item item);

    Item getItemByItemId(String itemId);

    @Cacheable(value = CacheValue.LISTS, key = "'itemList'")
    List<Item> getAllItems();

    @Cacheable(value = CacheValue.MAPS, key = "'itemMap'")
    Map<String, Item> getItemMap();

    @Cacheable(value = CacheValue.MAPS, key = "'allNameItemMap'")
    Map<String, Item> getAllNameItemMap();

}
