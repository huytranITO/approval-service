package com.msb.bpm.approval.appr.model.response.configuration;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryConditionResponse {
  private List<CategoryResp> category;
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CategoryResp {
    private String categoryDataCode;
    private Boolean categoryDataStatus;
    private String categoryCode;
    private Boolean categoryStatus;
    private List<ConditionDataResponse> conditionData;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ConditionDataResponse {
    private String conditionCategoryCode;
    private String conditionCategoryDataCode;
  }
}
