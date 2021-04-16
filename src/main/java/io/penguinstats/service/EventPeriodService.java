package io.penguinstats.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.constant.Constant.CacheValue;
import io.penguinstats.model.EventPeriod;

public interface EventPeriodService {

    @Caching(evict = {@CacheEvict(value = CacheValue.LISTS, key = "'eventPeriodList'")})
    public void saveEventPeriod(EventPeriod eventPeriod);

    @Cacheable(value = CacheValue.LISTS, key = "'eventPeriodList'")
    public List<EventPeriod> getAllSortedEventPeriod();

}
