package com.msb.bpm.approval.appr.model.request.customer;

import java.util.List;
import lombok.Data;

@Data
public class CommonCustomerRequest {
  private Customer customer;
  private List<IdentityDocument> identityDocuments;
  private List<Address> addresses;
}