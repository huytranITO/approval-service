package com.msb.bpm.approval.appr.model.response.oprisk;

import com.msb.bpm.approval.appr.model.response.legacy.LegacyBaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncOpRiskAssetResponse {
  private LegacyBaseResponse<CheckBlackListResDomain> checkBlackListC;
}
