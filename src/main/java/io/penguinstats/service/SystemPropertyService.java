package io.penguinstats.service;

import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import io.penguinstats.constant.Constant.CacheValue;
import io.penguinstats.model.SystemProperty;

public interface SystemPropertyService {

    @CacheEvict(value = CacheValue.MAPS, key = "'propertiesMap'")
    void saveProperty(SystemProperty property);

    @CacheEvict(value = CacheValue.MAPS, key = "'propertiesMap'")
    void saveProperty(String key, String value);

    SystemProperty getPropertyByKey(String key);

    String getPropertyStringValue(String key);

    String getPropertyStringValue(String key, String defaultValue);

    Integer getPropertyIntegerValue(String key);

    Integer getPropertyIntegerValue(String key, Integer defaultValue);

    Long getPropertyLongValue(String key);

    Long getPropertyLongValue(String key, Long defaultValue);

    @Cacheable(value = CacheValue.MAPS, key = "'propertiesMap'")
    Map<String, String> getPropertiesMap();

}
