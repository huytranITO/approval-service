package com.msb.bpm.approval.appr.service.cache;

import static com.msb.bpm.approval.appr.constant.Constant.Cache.MERCURY_CATEGORY_DATA;

import com.msb.bpm.approval.appr.client.configuration.MercuryClient;
import com.msb.bpm.approval.appr.model.response.configuration.MercuryDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 26/7/2023, Wednesday
 **/
@Component
@RequiredArgsConstructor
public class MercuryConfigurationServiceCache {

  private final MercuryClient mercuryClient;

  @Cacheable(cacheNames = MERCURY_CATEGORY_DATA, key = "#code", unless = "#result == null")
  public MercuryDataResponse searchPlace(String code) {
    return mercuryClient.searchPlace(code);
  }
}
