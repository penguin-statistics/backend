package io.penguinstats.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Server {
	CN("cn"), US("us"), JP("jp"), KR("kr");

	private String name;
}
