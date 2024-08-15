package com.msb.bpm.approval.appr.model.request.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoreCustomer {
  private String cifNumber;
  private String customerName;
  private String idNo;
  private String idType;
  private String dateOfBirthOrIncorp;
  private String issuedDate;
  private String placeOfBirthOrRegistration;
  private String issuedPlace;
  private String email;
  private String constOrBusinessType;
  private String sfTemp;
  private String addressLine;
  private String gender;
  private String nationality;
  private String maritalStatus;
  private String mobilePhone;
  private String vipCode;
  private String sfCode;
}
