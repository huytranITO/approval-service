package com.msb.bpm.approval.appr.model.request.customereb;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
public class CreateCustomerEbRequest {

  private CustomerEb customer;
  private List<IdentityDocumentEbRequest> identityDocuments;
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @With
  public static class CustomerEb {
    private String name;
  }
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @With
  public static class IdentityDocumentEbRequest {
    private String identityNumber;
    private String type;
    private Boolean primary;
  }

}
