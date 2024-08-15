package com.msb.bpm.approval.appr.model.response.legacy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LegacyRespMessage {

  private Long respCode;
  private String respDesc;
}
