package com.msb.bpm.approval.appr.model.response.customer;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRBCustomerResponse {

  private RBCustomerDetailResponse customer;

  private List<IdentityDocumentResponse> identityDocuments;

  private List<AddressResponse> addresses;
}
