package com.msb.bpm.approval.appr.model.request.css;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetScoreRBLegacyInfo {

  private String profileId;

  private String customerType;

  private String legalDocNo;

}
