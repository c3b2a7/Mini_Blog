package me.lolicom.blog;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lolicom
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
public class RedisConfig {
    
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> target.getClass().getName() +
                "." +
                method.getName() +
                "(" +
                Stream.of(params)
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")) +
                ")";
    }
    
    /**
     * using {@link RedisSerializer#json()} as ValueSerializer
     * using {@link RedisSerializer#string()} as KeySerializer
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        
        return redisTemplate;
    }
    
    /**
     * using {@link RedisSerializer#json()} as ValueSerializer
     * using {@link RedisSerializer#string()} as KeySerializer
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        
        stringRedisTemplate.setValueSerializer(RedisSerializer.json());
        stringRedisTemplate.setHashValueSerializer(RedisSerializer.json());
        
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }
    
    
    /**
     * A configuration of {@link org.springframework.data.redis.cache.RedisCache} for
     * {@link org.springframework.data.redis.cache.RedisCacheManager}
     *
     * @param properties a given {@link CacheProperties}
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties properties) {
        CacheProperties.Redis redisProperties = properties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }
}
