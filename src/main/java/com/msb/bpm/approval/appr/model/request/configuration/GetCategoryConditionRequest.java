package com.msb.bpm.approval.appr.model.request.configuration;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetCategoryConditionRequest {
  private String categoryCode;
  private List<String> categoryDataCode;
}
