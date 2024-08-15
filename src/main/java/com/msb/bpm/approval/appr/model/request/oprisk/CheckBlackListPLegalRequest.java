package com.msb.bpm.approval.appr.model.request.oprisk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
public class CheckBlackListPLegalRequest {
  private CheckBlackListPLegalData checkBlackListC;

}
