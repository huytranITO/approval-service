package com.msb.bpm.approval.appr.client.core.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreCustomerResponseData {

  private EsbMessage esbMessage;

  private CoreCustomerInfo customer;
}
