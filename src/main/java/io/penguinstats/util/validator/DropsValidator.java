package io.penguinstats.util.validator;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.DropType;
import io.penguinstats.model.DropInfo;
import io.penguinstats.model.TypedDrop;
import io.penguinstats.service.DropInfoService;

@Component("dropsValidator")
public class DropsValidator extends BaseValidator {

	private static Logger logger = LogManager.getLogger(DropsValidator.class);

	private DropInfoService dropInfoService;

	public DropsValidator(ValidatorContext context, DropInfoService dropInfoService) {
		super(context);
		this.dropInfoService = dropInfoService;
	}

	@Override
	public boolean validate() {
		List<TypedDrop> drops = this.context.getDrops();
		if (drops == null)
			return false;

		Map<String, List<DropInfo>> openingDropInfosMap =
				dropInfoService.getOpeningDropInfosMap(context.getServer(), context.getTimestamp());

		if (!openingDropInfosMap.containsKey(context.getStageId())) // the stage was not open at the given timestamp
			return false;

		List<DropInfo> allDropInfos = openingDropInfosMap.get(context.getStageId());
		Map<DropType, List<DropInfo>> dropInfoMapByDropType =
				allDropInfos.stream().collect(groupingBy(DropInfo::getDropType));
		Map<DropType, List<TypedDrop>> typedDropMapByDropType =
				drops.stream().collect(groupingBy(TypedDrop::getDropType));

		for (DropType dropType : DropType.values()) {
			if (!dropInfoMapByDropType.containsKey(dropType)) {
				logger.warn("Failed to find " + dropType + " drop info.");
				continue;
			}

			List<TypedDrop> typedDrops = typedDropMapByDropType.getOrDefault(dropType, new ArrayList<>());
			Map<String, TypedDrop> typedDropMapByItemId;
			try {
				typedDropMapByItemId = typedDrops.stream().collect(toMap(TypedDrop::getItemId, typedDrop -> typedDrop));
			} catch (IllegalStateException ex) {
				if (ex.getMessage().contains("Duplicate key")) {
					logger.debug("Found duplicated itemId in drops.");
					return false;
				} else
					throw ex;
			}

			final int typesNum = typedDrops.size();

			Set<String> uncheckedItemIds = typedDropMapByItemId.keySet();

			List<DropInfo> dropInfos = dropInfoMapByDropType.get(dropType);
			for (DropInfo dropInfo : dropInfos) {
				final int numberToCheck = dropInfo.getItemId() == null ? typesNum
						: Optional.ofNullable(typedDropMapByItemId.get(dropInfo.getItemId()))
								.map(TypedDrop::getQuantity).orElse(0);
				boolean judge = Optional.ofNullable(dropInfo.getBounds()).map(bounds -> bounds.isValid(numberToCheck))
						.orElse(true);
				if (!judge) {
					String targetName = dropInfo.getItemId() == null ? "Item types num in " + dropType
							: "Item " + dropInfo.getItemId() + "'s quantity in " + dropType;
					logger.debug("Failed target: " + targetName + " = " + numberToCheck);
					logger.debug("Bounds: " + dropInfo.getBounds().toString());
					return false;
				}
				if (dropInfo.getItemId() != null)
					uncheckedItemIds.remove(dropInfo.getItemId());
			}
			if (!uncheckedItemIds.isEmpty()) {
				logger.debug("Found unexpected items in " + dropType + ": " + uncheckedItemIds.toString());
				return false;
			}
		}
		return true;
	}

}
