package io.penguinstats.service;

import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import io.penguinstats.model.SystemProperty;

public interface SystemPropertyService {

    @CacheEvict(value = "maps", key = "'propertiesMap'")
    void saveProperty(SystemProperty property);

    @CacheEvict(value = "maps", key = "'propertiesMap'")
    void saveProperty(String key, String value);

    SystemProperty getPropertyByKey(String key);

    String getPropertyStringValue(String key);

    String getPropertyStringValue(String key, String defaultValue);

    Integer getPropertyIntegerValue(String key);

    Integer getPropertyIntegerValue(String key, Integer defaultValue);

    Long getPropertyLongValue(String key);

    Long getPropertyLongValue(String key, Long defaultValue);

    @Cacheable(value = "maps", key = "'propertiesMap'")
    Map<String, String> getPropertiesMap();

}
