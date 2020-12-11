package io.penguinstats.configuration;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.maxmind.db.Reader;
import com.maxmind.geoip2.DatabaseReader;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
public class GeoLocationConfig {

	private static DatabaseReader reader = null;
	private final ResourceLoader resourceLoader;

	public GeoLocationConfig(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Bean
	public DatabaseReader databaseReader() {
		try {
			log.info("GeoLocationConfig: Trying to load GeoLite2-Country database...");

			Resource resource = resourceLoader.getResource("classpath:maxmind/GeoLite2-City.mmdb");
			InputStream dbAsStream = resource.getInputStream();

			log.info("GeoLocationConfig: Database was loaded successfully.");

			// Initialize the reader
			return reader = new DatabaseReader.Builder(dbAsStream).fileMode(Reader.FileMode.MEMORY).build();

		} catch (IOException | NullPointerException e) {
			log.error("Database reader cound not be initialized. ", e);
			return null;
		}
	}

}