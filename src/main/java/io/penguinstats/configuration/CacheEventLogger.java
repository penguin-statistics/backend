package io.penguinstats.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

public class CacheEventLogger implements CacheEventListener<Object, Object> {

	private static Logger logger = LogManager.getLogger(CacheEventLogger.class);

	@Override
	public void onEvent(CacheEvent<?, ?> cacheEvent) {
		logger.info("Caching event {} {}", cacheEvent.getType(), cacheEvent.getKey());
	}

}
