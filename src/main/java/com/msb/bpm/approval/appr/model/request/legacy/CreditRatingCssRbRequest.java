package com.msb.bpm.approval.appr.model.request.legacy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditRatingCssRbRequest {

  private String channel;

  private String profileId;

  private String customerType;

  private String legalDocNo;

}
