package com.msb.bpm.approval.appr.model.request.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCustomerRequest extends CommonCustomerRequest {
  private CoreCustomer coreCustomer;
}