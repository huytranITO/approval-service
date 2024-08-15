package com.msb.bpm.approval.appr.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.model.dto.cic.CICDataCache;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@AllArgsConstructor
public class CICCache {

  private static final String KEY_PREFIX = "SYN_CIC_DATA_%s";
  private static final Long TIMEOUT = 8L;

  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  public CICDataCache get(String applicationBpmId) {
    String key = getKey(applicationBpmId);
    String value = redisTemplate.opsForValue().get(key);
    if (!StringUtils.hasText(value)) {
      return null;
    }
    return JsonUtil.convertString2Object(value, CICDataCache.class, objectMapper);
  }

  public void set(String applicationBpmId, CICDataCache cicDataCache) {
    String key = getKey(applicationBpmId);
    String value = JsonUtil.convertObject2String(cicDataCache, objectMapper);
    redisTemplate.opsForValue().set(key, value, TIMEOUT, TimeUnit.MINUTES);
  }

  private String getKey(String applicationBpmId) {
    return String.format(KEY_PREFIX, applicationBpmId);
  }
}
