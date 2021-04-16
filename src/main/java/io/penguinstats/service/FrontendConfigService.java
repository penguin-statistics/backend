package io.penguinstats.service;

import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import io.penguinstats.constant.Constant.CacheValue;
import io.penguinstats.model.FrontendConfig;

public interface FrontendConfigService {

    @CacheEvict(value = CacheValue.MAPS, key = "'frontendConfigMap'")
    void saveFrontendConfig(FrontendConfig config);

    @CacheEvict(value = CacheValue.MAPS, key = "'frontendConfigMap'")
    void saveFrontendConfig(String key, String value);

    @Cacheable(value = CacheValue.MAPS, key = "'frontendConfigMap'")
    Map<String, String> getFrontendConfigMap();

}
