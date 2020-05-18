package io.penguinstats.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.model.EventPeriod;

public interface EventPeriodService {

	@Caching(evict = {@CacheEvict(value = "lists", key = "'eventPeriodList'")})
	public void saveEventPeriod(EventPeriod eventPeriod);

	@Cacheable(value = "lists", key = "'eventPeriodList'")
	public List<EventPeriod> getAllSortedEventPeriod();

}
