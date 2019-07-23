package io.penguinstats.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.Item;

@Repository(value = "itemDao")
public class ItemDaoImpl implements ItemDao {

	@Resource
	private MongoTemplate mongoTemplate;

	@Override
	public void save(Item item) {
		mongoTemplate.save(item);
	}

	@Override
	public void removeItem(String itemId) {
		Query query = Query.query(Criteria.where("itemId").is(itemId));
		mongoTemplate.remove(query, Item.class);
	}

	@Override
	public void updateItem(Item item) {
		Query query = new Query(Criteria.where("id").is(item.getId()));

		Update update = new Update();
		update.set("itemId", item.getItemId());
		update.set("name", item.getName());
		update.set("sortId", item.getSortId());
		update.set("rarity", item.getRarity());
		update.set("iconUrl", item.getIconUrl());
		update.set("itemType", item.getItemType());
		update.set("addTimePoint", item.getAddTimePoint());
		update.set("spriteCoord", item.getSpriteCoord());

		mongoTemplate.updateFirst(query, update, Item.class);
	}

	@Override
	public List<Item> findAll() {
		return mongoTemplate.findAll(Item.class);
	}

	@Override
	public Item findItemByItemId(String itemId) {
		Query query = new Query(Criteria.where("itemId").is(itemId));
		Item item = mongoTemplate.findOne(query, Item.class);
		return item;
	}

}
