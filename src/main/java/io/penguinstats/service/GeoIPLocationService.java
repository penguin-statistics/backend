package io.penguinstats.service;

import java.io.IOException;

import com.maxmind.geoip2.exception.GeoIp2Exception;

import io.penguinstats.model.GeoIP;

public interface GeoIPLocationService {

	GeoIP getIpLocation(String ip) throws IOException, GeoIp2Exception;

	boolean isFromChinaMainland(String ip);

}
