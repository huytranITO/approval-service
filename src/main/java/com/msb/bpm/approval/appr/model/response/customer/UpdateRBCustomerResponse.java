package com.msb.bpm.approval.appr.model.response.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRBCustomerResponse extends CreateRBCustomerResponse {
  private CoreCustomerResponse coreCustomer;
}
