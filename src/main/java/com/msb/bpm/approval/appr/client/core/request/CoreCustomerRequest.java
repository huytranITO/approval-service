package com.msb.bpm.approval.appr.client.core.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreCustomerRequest {

  private CommonRequest commonInfo;

  private String cifNumber;

  private String idNumber;

}
