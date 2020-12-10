package io.penguinstats.model;

import lombok.Data;

@Data
public class GeoIP {

	private String ipAddress;
	private String country;
	private String city;

}