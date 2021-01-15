package io.penguinstats.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.penguinstats.util.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.dao.ItemDao;
import io.penguinstats.enums.ErrorCode;
import io.penguinstats.model.Item;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.penguinstats.util.exception.NotFoundException;

@Service("itemService")
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDao itemDao;

    @Override
    public void saveItem(Item item) {
        itemDao.save(item);
    }

    @Override
    public Item getItemByItemId(String itemId) {
        return itemDao.findByItemId(itemId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND,
                "Item[" + itemId + "] is not found", Optional.of(itemId)));
    }

    /**
     * @return List<Item>
     * @Title: getAllItems
     * @Description: Return all items in the database as a list.
     */
    @Override
    public List<Item> getAllItems() {
        List<Item> items = itemDao.findAll();
        LastUpdateTimeUtil.setCurrentTimestamp(LastUpdateMapKeyName.ITEM_LIST);
        return items;
    }

    /**
     * @return Map<String, Item>
     * @Title: getItemMap
     * @Description: Return a map which has itemId as key and item object as value.
     */
    @Override
    public Map<String, Item> getItemMap() {
        List<Item> list = getAllItems();
        Map<String, Item> map = new HashMap<>();
        list.forEach(item -> map.put(item.getItemId(), item));
        return map;
    }

    /**
     * @return Map<String, Item>
     * @Title: getAllNameItemMap
     * @Description: Return a map which has item name has key and item object as value. The item name comes from all languages.
     */
    @Override
    public Map<String, Item> getAllNameItemMap() {
        Map<String, Item> result = new HashMap<>();
        getAllItems().forEach(item -> {
            if (item.getNameMap() != null)
                item.getNameMap().values().forEach(name -> result.put(name, item));
        });
        return result;
    }

}
