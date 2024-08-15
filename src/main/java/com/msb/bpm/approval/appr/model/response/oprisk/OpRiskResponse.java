package com.msb.bpm.approval.appr.model.response.oprisk;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpRiskResponse {
  private String applicationId;
  private String identifierCode;
  private String queryType;
  private String resultCode;
  private String resultDescription;
  private String reasonInList;
  private String startDate;
  private String endDate;
  private String assetGroup;
  private String assetType;
  private LocalDateTime createdAt;
}
