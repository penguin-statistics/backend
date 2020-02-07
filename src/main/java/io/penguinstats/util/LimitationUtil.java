package io.penguinstats.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.penguinstats.constant.Constant;
import io.penguinstats.model.Drop;
import io.penguinstats.model.Item;
import io.penguinstats.model.ItemQuantityBounds;
import io.penguinstats.model.Limitation;
import io.penguinstats.model.Stage;
import io.penguinstats.model.Zone;
import io.penguinstats.service.LimitationService;
import io.penguinstats.service.StageService;
import io.penguinstats.service.ZoneService;

@Component("limitationUtil")
public class LimitationUtil {

	private static LimitationUtil limitationUtil;

	@Autowired
	private LimitationService limitationService;
	@Autowired
	private ZoneService zoneService;
	@Autowired
	private StageService stageService;

	@PostConstruct
	public void init() {
		limitationUtil = this;
		limitationUtil.limitationService = this.limitationService;
		limitationUtil.zoneService = this.zoneService;
		limitationUtil.stageService = this.stageService;
	}

	/**
	 * @Title: checkDrops
	 * @Description: Check if a list of drop is reliable or not. The drop list may contain 'furni'. This function
	 *               ignores item's addTime.
	 * @param drops
	 * @param stageId
	 * @return boolean
	 */
	public boolean checkDrops(List<Drop> drops, String stageId, long timestamp) {
		// first check if the zone is open or not
		Zone zone = zoneService.getZoneByStageId(stageId);
		if (zone == null || !zone.isInTimeRange(timestamp))
			return false;

		// for gacha type stage, we do not check drops
		Stage stage = stageService.getStageByStageId(stageId);
		if (stage != null) {
			Boolean isGacha = stage.getIsGacha();
			if (isGacha != null && isGacha) {
				return true;
			}
		}

		// then check limitation
		Limitation limitation = limitationService.getExtendedLimitation(stageId);
		if (limitation == null)
			return true;

		// get number of types excluding furniture
		boolean hasFurniture = false;
		for (Drop drop : drops) {
			if (drop.getItemId().equals("furni")) {
				hasFurniture = true;
				break;
			}
		}
		int typesNum = hasFurniture ? drops.size() - 1 : drops.size();

		// check type bounds
		if (limitation.getItemTypeBounds() != null && !limitation.getItemTypeBounds().isValid(typesNum))
			return false;

		// check quantity bounds for every item in the limitation (Note: not in the drop)
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
