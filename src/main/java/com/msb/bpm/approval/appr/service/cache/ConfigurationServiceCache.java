package com.msb.bpm.approval.appr.service.cache;

import static com.msb.bpm.approval.appr.constant.Constant.Cache.CONFIGURATION_CATEGORY_DATA;

import com.msb.bpm.approval.appr.client.configuration.ConfigurationListClient;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse.Detail;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 24/7/2023, Monday
 **/
@Component
@RequiredArgsConstructor
public class ConfigurationServiceCache {

  private final ConfigurationListClient configurationListClient;

  @Cacheable(cacheNames = CONFIGURATION_CATEGORY_DATA, key = "#code", unless = "#result == null")
  public List<CategoryDataResponse> getCategoryDataByCode(ConfigurationCategory code) {
    return configurationListClient.findByCategoryCode(code, HeaderUtil.getToken());
  }

  @Cacheable(cacheNames = CONFIGURATION_CATEGORY_DATA, key = "#codes", unless = "#result == null")
  public Map<String,List<Detail>> getCategoryDataByCodes(List<String> codes) {
    return configurationListClient.findByListCategoryDataCodes(codes, HeaderUtil.getToken())
        .getValue();
  }

  public Map<String, String> getCategoryData(Map<String, List<Detail>> categoryMap, ConfigurationCategory category) {
    return categoryMap.get(category.getCode())
        .stream().collect(Collectors.toMap(Detail::getCode, Detail::getValue));
  }
}
