package io.penguinstats.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DropMatrixElementType {

	REGULAR("reular"), TREND("trend");

	private String name;

}
