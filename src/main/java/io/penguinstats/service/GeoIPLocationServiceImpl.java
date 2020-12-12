package io.penguinstats.service;

import static java.util.Objects.nonNull;

import java.io.IOException;
import java.net.InetAddress;

import org.springframework.stereotype.Service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import io.penguinstats.model.GeoIP;

@Service("geoIPLocationService")
public class GeoIPLocationServiceImpl implements GeoIPLocationService {

	private final DatabaseReader databaseReader;

	public GeoIPLocationServiceImpl(DatabaseReader databaseReader) {
		this.databaseReader = databaseReader;
	}

	/**
	 * get user position by ip address
	 *
	 * @param ip String ip address
	 * @return UserPositionDTO model
	 * @throws IOException     if local database city not exist
	 * @throws GeoIp2Exception if cannot get info by ip address
	 */
	@Override
	public GeoIP getIpLocation(String ip) throws IOException, GeoIp2Exception {
		GeoIP position = new GeoIP();
		InetAddress ipAddress = InetAddress.getByName(ip);
		CityResponse cityResponse = databaseReader.city(ipAddress);
		if (nonNull(cityResponse) && nonNull(cityResponse.getCity())) {
			String country = (cityResponse.getCountry() != null) ? cityResponse.getCountry().getName() : "";
			position.setCountry(country);
			position.setCity(cityResponse.getCity().getName());
			position.setIpAddress(ip);
		}
		return position;
	}

	@Override
	public boolean isFromChinaMainland(String ip) {
		try {
			GeoIP geoip = getIpLocation(ip);
			return "China".equalsIgnoreCase(geoip.getCountry());
		} catch (IOException | GeoIp2Exception e) {
			return false;
		}
	}

}
