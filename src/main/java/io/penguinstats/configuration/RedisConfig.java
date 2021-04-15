package io.penguinstats.configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.penguinstats.constant.Constant.CacheValue;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        RedisSerializer<String> redisSerializer = new StringRedisSerializer();

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);
        MyObjectMapper objectMapper = new MyObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        template.setConnectionFactory(factory);
        template.setKeySerializer(redisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(redisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);
        MyObjectMapper objectMapper = new MyObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(1L))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        Set<String> cacheNames = new HashSet<>();
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        Map<String, Duration> durationMap = new HashMap<>();

        durationMap.put(CacheValue.USERS, Duration.ofDays(1L));
        durationMap.put(CacheValue.DROP_MATRIX, Duration.ZERO);
        durationMap.put(CacheValue.SEGMENTED_DROP_MATRIX, Duration.ZERO);
        durationMap.put(CacheValue.PATTERN_MATRIX, Duration.ZERO);
        durationMap.put(CacheValue.DROP_INFO_LIST, Duration.ofDays(1L));
        durationMap.put(CacheValue.DROP_SET, Duration.ofDays(1L));
        durationMap.put(CacheValue.LATEST_DROP_INFO_MAP, Duration.ofDays(1L));
        durationMap.put(CacheValue.LATEST_MAX_ACCUMULATABLE_TIME_RANGE_MAP, Duration.ofDays(1L));
        durationMap.put(CacheValue.LATEST_TIME_RANGE_MAP, Duration.ofDays(1L));
        durationMap.put(CacheValue.TOTAL_STAGE_TIMES_MAP, Duration.ZERO);
        durationMap.put(CacheValue.TOTAL_ITEM_QUANTITIES_MAP, Duration.ZERO);
        durationMap.put(CacheValue.LISTS, Duration.ofDays(1L));
        durationMap.put(CacheValue.MAPS, Duration.ofDays(1L));

        durationMap.forEach((cacheName, duration) -> {
            cacheNames.add(cacheName);
            configMap.put(cacheName, config.entryTtl(duration));
        });

        RedisCacheManager cacheManager = RedisCacheManager.builder(factory).cacheDefaults(config)
                .initialCacheNames(cacheNames).withInitialCacheConfigurations(configMap).build();
        return cacheManager;
    }

    public class MyObjectMapper extends ObjectMapper {
        private static final long serialVersionUID = 1L;

        public MyObjectMapper() {
            super();
            // Remove the parsing of various @JsonSerialize annotations
            this.configure(MapperFeature.USE_ANNOTATIONS, false);
            // serialize only for non-null values
            this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            // serialize the type into the property json string
            this.enableDefaultTyping(DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
            // Ignore the error when the matching property is not found
            this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // The bean that does not contain any attributes is not reported incorrectly.
            this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        }
    }

}
