package com.msb.bpm.approval.appr.model.response.legacy.impl.aml;

import com.msb.bpm.approval.appr.model.response.legacy.LegacyBaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAMLInfoResponse {

  private LegacyBaseResponse<GetAMLInfoResDomain> getCheckAmlPublic;
}
