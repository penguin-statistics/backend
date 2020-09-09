package io.penguinstats.util;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.penguinstats.model.DropMatrixElement;

public class DropMatrixElementUtil {

	public static List<DropMatrixElement> combineElementLists(Collection<DropMatrixElement> elements1,
			Collection<DropMatrixElement> elements2) {
		Map<String, Map<String, DropMatrixElement>> convertedMap = convertElementsToMapByStageIdAndItemId(elements1);
		elements2.forEach(el -> {
			String stageId = el.getStageId();
			String itemId = el.getItemId();
			Map<String, DropMatrixElement> subMap = convertedMap.getOrDefault(stageId, new HashMap<>());
			DropMatrixElement element =
					subMap.getOrDefault(itemId, new DropMatrixElement(stageId, itemId, 0, 0, null, null));
			element.setQuantity(element.getQuantity() + el.getQuantity());
			element.setTimes(element.getTimes() + el.getTimes());
			subMap.put(itemId, element);
			convertedMap.put(stageId, subMap);
		});
		return convertMapByStageIdAndItemIdToElements(convertedMap);
	}

	private static Map<String, Map<String, DropMatrixElement>>
			convertElementsToMapByStageIdAndItemId(Collection<DropMatrixElement> elements) {
		Map<String, Map<String, DropMatrixElement>> result = new HashMap<>();
		Map<String, List<DropMatrixElement>> elementsMapByStageId1 =
				elements.stream().collect(groupingBy(el -> el.getStageId()));
		elementsMapByStageId1.forEach((stageId, els) -> {
			Map<String, DropMatrixElement> subMap = result.getOrDefault(stageId, new HashMap<>());
			Map<String, List<DropMatrixElement>> elementsMapByItemId =
					els.stream().collect(groupingBy(el -> el.getItemId()));
			elementsMapByItemId.forEach((itemId, els2) -> {
				subMap.put(itemId, els2.get(0));
			});
			result.put(stageId, subMap);
		});
		return result;
	}

	private static List<DropMatrixElement>
			convertMapByStageIdAndItemIdToElements(Map<String, Map<String, DropMatrixElement>> map) {
		return map.values().stream().flatMap(m -> m.values().stream()).collect(toList());
	}

}
