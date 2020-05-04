package io.penguinstats.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import io.penguinstats.enums.Server;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item implements Serializable {

	public interface ItemBaseView {};

	public interface ItemI18nView extends ItemBaseView {};

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;

	@Indexed
	@JsonView(ItemBaseView.class)
	private String itemId;

	@JsonView(ItemBaseView.class)
	private String name;

	@JsonProperty("name_i18n")
	@JsonView(ItemI18nView.class)
	private Map<Server, String> nameMap;

	@JsonView(ItemBaseView.class)
	private Integer sortId;

	@JsonView(ItemBaseView.class)
	private Integer rarity;

	@JsonView(ItemBaseView.class)
	private Map<Server, ItemExistence> existence;

	@JsonView(ItemBaseView.class)
	private String itemType;

	@JsonView(ItemBaseView.class)
	private Integer addTimePoint;

	@JsonView(ItemBaseView.class)
	private List<Integer> spriteCoord;

	public Item(String itemId, String name, Map<Server, String> nameMap, Integer sortId, Integer rarity,
			Map<Server, ItemExistence> existence, String itemType, Integer addTimePoint, List<Integer> spriteCoord) {
		this.itemId = itemId;
		this.name = name;
		this.nameMap = nameMap;
		this.sortId = sortId;
		this.rarity = rarity;
		this.existence = existence;
		this.itemType = itemType;
		this.addTimePoint = addTimePoint;
		this.spriteCoord = spriteCoord;
	}
}
