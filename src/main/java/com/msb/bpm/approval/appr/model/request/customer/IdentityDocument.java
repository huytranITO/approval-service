package com.msb.bpm.approval.appr.model.request.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdentityDocument {
  private Long id;
  private String type;
  private String identityNumber;
  private Boolean edit;
  private String issuedDate;
  private String issuedBy;
  private String issuedPlace;
  private String cif;
  private Boolean primary;
}
