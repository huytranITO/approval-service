package com.msb.bpm.approval.appr.model.response.customereb;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CustomerEbResponse {

  private CustomerEb customer;
  private CoreCustomerEb coreCustomer;
  private List<IdentityDocumentEbResponse> identityDocuments;
  private List<AddressEbResponse> addresses;
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CustomerEb {
    private Long id;
    private String bpmCif;
    private String name;
    private String email;
    private String userRM;
    private String typeOfCustomer;
    private String representative;
    private String customerSegmentation;
    private String typeOfBusiness;
    private String business;
    private String fieldCodeLevel5;
    private String customerType;
    private Integer version;
    private Boolean active;
    private LocalDateTime dateOfIncorp;
    private String national;
    private String businessSectorDetail;
  }
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CoreCustomerEb {
    private String cifNumber;
    private String customerName;
    private String idNo;
    private String idType;
    private LocalDateTime dateOfBirthOrIncorp;
    private LocalDateTime issuedDate;
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
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class IdentityDocumentEbResponse {
    private Long id;
    private String identityNumber;
    private String type;
    private String issuedPlace;
    private LocalDateTime issuedDate;
    private Integer numberOfChange;
    private LocalDateTime changedIssuedDate;
    private String cif;
    private Boolean primary;
  }
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AddressEbResponse {
    private Long id;
    private String addressType;
    private String cityCode;
    private String districtCode;
    private String wardCode;
    private String addressLine;
  }
}
