package com.msb.bpm.approval.appr.config.cache;

import static com.msb.bpm.approval.appr.constant.Constant.Cache.CONFIGURATION_CATEGORY_DATA;
import static com.msb.bpm.approval.appr.constant.Constant.Cache.ESB_ACCOUNT_INFO;
import static com.msb.bpm.approval.appr.constant.Constant.Cache.MERCURY_CATEGORY_DATA;

import com.msb.bpm.approval.appr.config.ApplicationDurationConfig;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 24/7/2023, Monday
 **/
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisCacheConfig {

  private final ApplicationDurationConfig config;
  @Bean
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
    return builder -> builder
        .withCacheConfiguration(CONFIGURATION_CATEGORY_DATA,
            RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                    SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(config.getCategoryDataDuration())))
        .withCacheConfiguration(MERCURY_CATEGORY_DATA,
            RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                    SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(config.getMercuryDataDuration())))
        .withCacheConfiguration(ESB_ACCOUNT_INFO,
            RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                    SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(config.getEsbAccountDuration())));
  }
}
