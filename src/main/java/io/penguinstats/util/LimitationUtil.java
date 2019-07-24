package io.penguinstats.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.penguinstats.model.Drop;
import io.penguinstats.model.Item;
import io.penguinstats.model.ItemQuantityBounds;
import io.penguinstats.model.Limitation;
import io.penguinstats.service.LimitationService;

@Component("limitationUtil")
public class LimitationUtil {

	@Autowired
	private LimitationService limitationService;
	private static LimitationUtil limitationUtil;

	@PostConstruct
	public void init() {
		limitationUtil = this;
		limitationUtil.limitationService = this.limitationService;
	}

	/**
	 * @Title: checkDrops
	 * @Description: Check if a list of drop is reliable or not. The drop list may contain 'furni'. This function
	 *               ignores item's addTime.
	 * @param drops
	 * @param stageId
	 * @return boolean
	 */
	public boolean checkDrops(List<Drop> drops, String stageId) {
		Limitation limitation = limitationService.getRealLimitation(stageId);
		if (limitation == null)
			return true;
		boolean hasFurniture = false;
		for (Drop drop : drops) {
			if (drop.getItemId().equals("furni")) {
				hasFurniture = true;
				break;
			}
		}
		int typesNum = hasFurniture ? drops.size() - 1 : drops.size();
		if (limitation.getItemTypeBounds() != null && !limitation.getItemTypeBounds().isValid(typesNum))
			return false;
		Map<String, Drop> dropsMap = new HashMap<>();
		for (Drop drop : drops)
			dropsMap.put(drop.getItemId(), drop);
		List<ItemQuantityBounds> itemQuantityBoundsList = limitation.getItemQuantityBounds();
		for (ItemQuantityBounds itemQuantityBounds : itemQuantityBoundsList) {
			Drop drop = dropsMap.get(itemQuantityBounds.getItemId());
			int quantity = drop == null ? 0 : drop.getQuantity();
			if (itemQuantityBounds.getBounds() != null && !itemQuantityBounds.getBounds().isValid(quantity))
				return false;
		}
		return true;
	}

	/**
	 * @Title: checkDrops
	 * @Description: Check drops with given limitationMap. This function does NOT ignore item's addTime.
	 * @param drops
	 * @param stageId
	 * @param limitationMap
	 * @return boolean
	 */
	public boolean checkDrops(List<Drop> drops, String stageId, Long timestamp, Map<String, Limitation> limitationMap,
			Map<String, Item> itemMap) {
		Limitation limitation = limitationMap.get(stageId);
		if (limitation == null)
			return true;
		boolean hasFurniture = false;
		for (Drop drop : drops) {
			if (drop.getItemId().equals("furni")) {
				hasFurniture = true;
				break;
			}
		}
		int typesNum = hasFurniture ? drops.size() - 1 : drops.size();
		Map<String, Drop> dropsMap = new HashMap<>();
		for (Drop drop : drops)
			dropsMap.put(drop.getItemId(), drop);
		boolean hasSpecialTimepoint = false;
		List<ItemQuantityBounds> itemQuantityBoundsList = limitation.getItemQuantityBounds();
		for (ItemQuantityBounds itemQuantityBounds : itemQuantityBoundsList) {
			String itemId = itemQuantityBounds.getItemId();
			Item item = itemMap.get(itemId);
			Integer addTimePoint = item.getAddTimePoint();
			if (addTimePoint != null && (timestamp <= Constant.ADD_TIME_POINTS[addTimePoint])) {
				hasSpecialTimepoint = true;
				continue;
			}
			Drop drop = dropsMap.get(itemId);
			int quantity = drop == null ? 0 : drop.getQuantity();
			if (itemQuantityBounds.getBounds() != null && !itemQuantityBounds.getBounds().isValid(quantity))
				return false;
		}
		if (!hasSpecialTimepoint && limitation.getItemTypeBounds() != null
				&& !limitation.getItemTypeBounds().isValid(typesNum))
			return false;
		return true;
	}

}
