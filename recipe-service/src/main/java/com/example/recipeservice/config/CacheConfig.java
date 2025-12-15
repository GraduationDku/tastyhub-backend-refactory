package com.example.recipeservice.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineSpec() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(5))
                .recordStats();
    }

    @Bean
    public CacheManager caffeineCacheManager(Caffeine<Object, Object> caffeineSpec) {
        CaffeineCacheManager mgr = new CaffeineCacheManager("popularRecipes","recipeDetail");
        mgr.setCaffeine(caffeineSpec);
        return mgr;
    }


    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // 기본 캐시 유효 시간 (예: 30분)
                .disableCachingNullValues()       // null 값은 캐싱하지 않음
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // Key 직렬화 (String)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())); // Value 직렬화 (JSON)

        // 3. RedisCacheManager 빌드
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();

    }

    @Bean
    public CacheManager cacheManager(CacheManager caffeineCacheManager, CacheManager redisCacheManager) {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager(caffeineCacheManager, redisCacheManager);
        compositeCacheManager.setFallbackToNoOpCache(false);
        return compositeCacheManager;
    }

}
