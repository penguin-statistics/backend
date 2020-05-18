package io.penguinstats.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Server {
//	china server
	CN("cn"),
//	the USA server
	US("us"),
// japan server
	JP("jp"),
//	korea server
	KR("kr");

	private String name;
}
