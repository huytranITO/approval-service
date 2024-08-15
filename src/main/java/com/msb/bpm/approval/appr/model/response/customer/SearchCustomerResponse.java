package com.msb.bpm.approval.appr.model.response.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCustomerResponse {

  private Customer customers;

  private CoreCustomer coreCustomer;

  @Data
  public static class Customer {
    private Long id;
    private String bpmCif;
    private String cif;
    private String name;
    private String identityNumber;
  }

  @Data
  public static class CoreCustomer {
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
}
