package io.penguinstats.configuration;

import io.penguinstats.util.LastUpdateTimeUtil;
import lombok.extern.log4j.Log4j2;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class CacheEventLogger implements CacheEventListener<Object, Object> {

	@Override
	public void onEvent(CacheEvent<?, ?> cacheEvent) {
		log.info("Caching event {} {}", cacheEvent.getType(), cacheEvent.getKey());
		if (EventType.CREATED.equals(cacheEvent.getType()) && cacheEvent.getKey() != null) {
			String key = cacheEvent.getKey().toString();
			LastUpdateTimeUtil.setCurrentTimestamp(key);
		}
	}

}
