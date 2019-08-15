package io.penguinstats.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "limitation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Limitation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;
	@Indexed
	private String name; // 'name' can be 'all', a stageId, or any identifier.
	private Bounds itemTypeBounds; // EXCLUDE furniture.
	private List<ItemQuantityBounds> itemQuantityBounds;
	private List<String> inheritance;

	public Limitation(String name, Bounds itemTypeBounds, List<ItemQuantityBounds> itemQuantityBounds,
			List<String> inheritance) {
		this.name = name;
		this.itemTypeBounds = itemTypeBounds;
		this.itemQuantityBounds = itemQuantityBounds;
		this.inheritance = inheritance;
	}

	/**
	 * @Title: getItemQuantityBoundsMap
	 * @Description: Return a map which has itemId as key and ItemQuantityBounds object as value.
	 * @return Map<String,ItemQuantityBounds>
	 */
	@JsonIgnore
	public Map<String, ItemQuantityBounds> getItemQuantityBoundsMap() {
		Map<String, ItemQuantityBounds> itemQuantityBoundsMap = new HashMap<>();
		if (this.itemQuantityBounds != null) {
			for (ItemQuantityBounds bounds : this.itemQuantityBounds)
				itemQuantityBoundsMap.put(bounds.getItemId(), bounds);
		}
		return itemQuantityBoundsMap;
	}

	/**
	 * @Title: merge
	 * @Description: Merge bounds in other limitation into this one. The bounds in this limitation will NOT be covered
	 *               by the other one. Inheritance will be ignored.
	 * @param otherLimitation
	 * @return void
	 */
	public void merge(Limitation otherLimitation) {
		if (this.itemTypeBounds == null && otherLimitation.itemTypeBounds != null)
			this.itemTypeBounds = otherLimitation.itemTypeBounds;

		Map<String, ItemQuantityBounds> mapThis = this.getItemQuantityBoundsMap();
		Map<String, ItemQuantityBounds> mapOther = otherLimitation.getItemQuantityBoundsMap();
		for (String itemId : mapOther.keySet()) {
			if (!mapThis.containsKey(itemId))
				mapThis.put(itemId, mapOther.get(itemId));
		}
		List<ItemQuantityBounds> newItemQuantityBounds = new ArrayList<>();
		for (String itemId : mapThis.keySet())
			newItemQuantityBounds.add(mapThis.get(itemId));
		this.itemQuantityBounds = newItemQuantityBounds;
	}

	/**
	 * @Title: filterItemQuantityBounds
	 * @Description: Remove all itemQuantity bounds whose itemId is not in the given itemIds.
	 * @param itemIds
	 * @return void
	 */
	public void filterItemQuantityBounds(Set<String> itemIds) {
		if (this.itemQuantityBounds == null)
			return;
		Iterator<ItemQuantityBounds> iter = this.itemQuantityBounds.iterator();
		while (iter.hasNext()) {
			String itemId = iter.next().getItemId();
			if (!itemIds.contains(itemId))
				iter.remove();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name).append("\n");
		if (this.itemTypeBounds != null) {
			sb.append("\tTypes: ").append(this.itemTypeBounds.toString()).append("\n");
		}
		if (this.itemQuantityBounds != null && !this.itemQuantityBounds.isEmpty()) {
			sb.append("\tItems: \n");
			for (ItemQuantityBounds itemQuantityBounds : this.itemQuantityBounds) {
				sb.append("\t\t").append(itemQuantityBounds.toString()).append("\n");
			}
		}
		if (this.inheritance != null && !this.inheritance.isEmpty()) {
			sb.append("\tInheritance: ").append(this.inheritance.toString()).append("\n");
		}
		return sb.toString();
	}

}
