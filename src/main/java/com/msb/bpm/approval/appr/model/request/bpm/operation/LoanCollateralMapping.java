package com.msb.bpm.approval.appr.model.request.bpm.operation;

import lombok.*;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
public class LoanCollateralMapping {
  private Long collateralId;
  private Long loanId;
}