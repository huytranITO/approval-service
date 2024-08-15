package com.msb.bpm.approval.appr.model.response.customer;

import java.time.LocalDate;
import lombok.Data;

@Data
public class CoreCustomerResponse {

  private String cifNumber;
  private String customerName;
  private String idNo;
  private String idType;
  private LocalDate dateOfBirthOrIncorp;
  private LocalDate issuedDate;
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
