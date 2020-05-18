package io.penguinstats.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DropType {

//	drop type is normal drop
	NORMAL_DROP("normal_drop"),
//	drop type is extra drop
	EXTRA_DROP("extra_drop"),
//	drop type is special drop
	SPECIAL_DROP("special_drop"),
//drop item is furniture
	FURNITURE("furniture");

	private String type;
}
