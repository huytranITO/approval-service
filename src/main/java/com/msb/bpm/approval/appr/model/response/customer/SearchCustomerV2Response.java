package com.msb.bpm.approval.appr.model.response.customer;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCustomerV2Response {

  private CustomerDetailResponse customer;
  private CoreCustomerResponse coreCustomer;
  private List<IdentityDocumentResponse> identityDocuments;
  private List<AddressDetailResponse> addresses;
  private List<Long> relatedCustomerIds;
}
