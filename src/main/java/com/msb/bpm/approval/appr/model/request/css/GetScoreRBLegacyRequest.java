package com.msb.bpm.approval.appr.model.request.css;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetScoreRBLegacyRequest {

  private CommonLegacyRequest common;

  private GetScoreRBLegacyInfo info;

}
