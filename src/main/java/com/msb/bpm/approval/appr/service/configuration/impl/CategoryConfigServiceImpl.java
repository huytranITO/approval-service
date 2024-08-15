package com.msb.bpm.approval.appr.service.configuration.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.configuration.ConfigurationListClient;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.model.dto.cic.CicBranchData;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.service.configuration.CategoryConfigService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryConfigServiceImpl implements CategoryConfigService {

  private final ConfigurationListClient configurationListClient;
  private final ObjectMapper objectMapper;

  @Override
  public Map<String, CicBranchData> getCICBranch(String token) {
    List<CategoryDataResponse> categoryDataResponses = configurationListClient.findByCategoryCode(
        ConfigurationCategory.CIC_BRANCH_DATA, token);

    if (CollectionUtils.isEmpty(categoryDataResponses)) {
      return new HashMap<>();
    }
    Map<String, CicBranchData> result = new HashMap<>();
    for (CategoryDataResponse categoryDataResponse : categoryDataResponses) {
      if (categoryDataResponse != null) {
        CicBranchData cicBranchData = JsonUtil.convertString2Object(
            categoryDataResponse.getValue(),
            CicBranchData.class, objectMapper);
        if (cicBranchData != null) {
          result.put(cicBranchData.getBpmDealingRoomCode(), cicBranchData);
        }
      }
    }
    return result;
  }
}
