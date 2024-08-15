package com.msb.bpm.approval.appr.model.request.customer;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
  private Long id;
  private String bpmCif;
  //private String cifNo;
  private String name;
  //private String identityNumber;
  //private String issuedBy;
  //private String issuedPlace;
  //private LocalDate issuedDate;
  private LocalDate birthday;
  //private String identityType;
  private String gender;
  private String national;
  private Integer maritalStatus;
  private String phoneNumber;
  private String email;
  private String customerSegment;
  private Boolean object;
  private String staffId;
}
