package io.penguinstats.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;
import org.springframework.stereotype.Component;

import io.penguinstats.util.LastUpdateTimeUtil;

@Component
public class CacheEventLogger implements CacheEventListener<Object, Object> {

	private static Logger logger = LogManager.getLogger(CacheEventLogger.class);

	@Override
	public void onEvent(CacheEvent<?, ?> cacheEvent) {
		logger.info("Caching event {} {}", cacheEvent.getType(), cacheEvent.getKey());
		if (EventType.CREATED.equals(cacheEvent.getType()) && cacheEvent.getKey() != null) {
			String key = cacheEvent.getKey().toString();
			if ("itemList".equals(key)) {
				LastUpdateTimeUtil.setCurrentTimestamp("itemList");
			} else if ("zoneList".equals(key)) {
				LastUpdateTimeUtil.setCurrentTimestamp("zoneList");
			} else if ("stageList".equals(key)) {
				LastUpdateTimeUtil.setCurrentTimestamp("stageList");
			} else if ("limitationMap".equals(key)) {
				LastUpdateTimeUtil.setCurrentTimestamp("limitationMap");
			}
		}
	}

}
