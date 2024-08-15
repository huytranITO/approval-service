package com.msb.bpm.approval.appr.config.card.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CheckCardCreatedRequest {
  private String cifNumber;
  private String policyCode;
}
