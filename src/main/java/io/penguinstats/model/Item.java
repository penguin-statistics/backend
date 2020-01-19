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
	private Map<String, String> nameMap;

	@JsonView(ItemBaseView.class)
	private Integer sortId;

	@JsonView(ItemBaseView.class)
	private Integer rarity;

	@Deprecated
	@JsonIgnore
	private String iconUrl;

	@JsonView(ItemBaseView.class)
	private String itemType;

	@JsonView(ItemBaseView.class)
	private Integer addTimePoint;

	@JsonView(ItemBaseView.class)
	private List<Integer> spriteCoord;

	public Item(String itemId, String name, Map<String, String> nameMap, Integer sortId, Integer rarity, String iconUrl,
			String itemType, Integer addTimePoint, List<Integer> spriteCoord) {
		this.itemId = itemId;
		this.name = name;
		this.nameMap = nameMap;
		this.sortId = sortId;
		this.rarity = rarity;
		this.iconUrl = iconUrl;
		this.itemType = itemType;
		this.addTimePoint = addTimePoint;
		this.spriteCoord = spriteCoord;
	}
}
