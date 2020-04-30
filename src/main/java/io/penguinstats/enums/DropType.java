package io.penguinstats.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DropType {
	NORMAL_DROP("normal_drop"), EXTRA_DROP("extra_drop"), SPECIAL_DROP("special_drop"), FURNITURE("furniture");

	private String type;
}
