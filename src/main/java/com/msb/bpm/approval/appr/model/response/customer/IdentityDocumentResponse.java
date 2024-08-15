package com.msb.bpm.approval.appr.model.response.customer;

import java.time.LocalDate;
import lombok.Data;

@Data
public class IdentityDocumentResponse {

  private Long id;
  private String type;
  private String identityNumber;
  private String issuedBy;
  private String issuedPlace;
  private LocalDate issuedDate;
  private String cif;
  private boolean primary;
}
