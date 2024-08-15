package com.msb.bpm.approval.appr.client.core.response;


import com.msb.bpm.approval.appr.enums.legacy.YesNo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreCustomerInfo {

  private String cifNumber;
  private String status;
  private String bankCode;
  private String branchCode;
  private String shortName;
  private String customerName;
  private String addCustomerName;
  private String motherMaidenName;
  private String foreignAddress;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String addressLine5;
  private String provincePostalCode;
  private String individual;
  private String idNo;
  private String idType;

  private String issuedDate;
  private String issuedPlace;
  private String issuedCountry;

  private String dateOfBirthOrIncorp;

  private String placeOfBirthOrRegistration;
  private String nationality;
  private String raceCode;
  private String gender;
  private String maritalStatus;
  private String maritalDate;
  private String homePhoneNo;
  private String offcicePhoneNo;
  private String handPhoneNo;
  private String residentCode;
  private String email;
  private String mobilePhone;
  private String fax;
  private String tax;
  private String reviewDate;
  private String constOrBusinessType;
  private String combineCycle;
  private String inquiryIDCode;
  private String sfTemp1;
  private String sfTemp2;
  private String sfTemp3;
  private String sfTemp4;
  private String sfTemp5;
  private String sfTemp6;
  private String sfTemp7;
  private String sfTemp8;
  private String sfCode1;
  private String sfCode2;
  private String sfCode3;
  private String sfCode4;
  private String sfCode5;
  private String sfCode6;
  private String sfCode7;
  private String sfCode8;
  private String cusInfTem1;
  private String cusInfTem2;
  private String cusInfTem3;
  private String cusInfTem4;
  private String cusInfTem5;
  private String cusInfTem6;
  private String cusInfTem7;
  private String cusInfTem8;
  private String cusInfCode1;
  private String cusInfCode2;
  private String cusInfCode3;
  private String cusInfCode4;
  private String cusInfCode5;
  private String cusInfCode6;
  private String cusInfCode7;
  private String cusInfCode8;
  private String religionCode;
  private String langIndentifier;
  private String customerRatingCode;
  private String customerStatus;
  private String vipCode;
  private String insiderCode;
  private String salutation;
  private String businessOrOccuptionCode;

  public boolean isIndividualCustomer() {
    return YesNo.isYes(individual);
  }

//  public LocalDate getIssuedDate() {
//    return DateTimeUtil.parseCoreTime_ddMMyy(issuedDate);
//  }
//
//  public LocalDate getDateOfBirthOrIncorp() {
//    return DateTimeUtil.parseCoreTime_ddMMyy(dateOfBirthOrIncorp);
//  }
}
