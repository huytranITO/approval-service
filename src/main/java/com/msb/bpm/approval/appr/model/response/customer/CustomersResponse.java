package com.msb.bpm.approval.appr.model.response.customer;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomersResponse {
  private List<CustomersRBResponse> customersRb;
  private List<CustomersEBResponse> customersEb;
  @Data
  public static class CustomersRBResponse {
    private CustomerResponse customer;
    private List<IdentityDocuments> identityDocuments;
    private List<Addresses> addresses;
    private List<Long> relatedCustomerIds;
    @Data
    public static class CustomerResponse {
      private Long id;
      private String bpmCif;
      private String name;
      private String birthday;
      private String gender;
      private String national;
      private String maritalStatus;
      private String phoneNumber;
      private String email;
      private String customerSegment;
      private String staffId;
      private String version;
      private String customerType;
      private boolean active;
    }

    @Data
    public static class IdentityDocuments {
      private Long id;
      private String identityNumber;
      private String type;
      private String issuedBy;
      private String issuedPlace;
      private String issuedDate;
      private String cif;
      private boolean primary;
    }
    @Data
    public static class Addresses {
      private Long id;
      private String addressType;
      private String cityCode;
      private String cityName;
      private String districtCode;
      private String districtName;
      private String wardCode;
      private String wardName;
      private String addressLine;
      private String fullAddress;
    }
  }

  @Data
  public static class CustomersEBResponse {
    private CustomerEbRes customer;
    private List<IdentityDocumentsEbRes> identityDocuments;
    private List<AddressEbRes> addresses;
    private List<Long> relatedCustomerIds;
  }
  @Data
  public static class CustomerEbRes {
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
    private String dateOfIncorp;
    private String national;
    private String businessSectorDetail;
  }
  @Data
  public static class IdentityDocumentsEbRes {
    private Long id;
    private String identityNumber;
    private String type;
    private String issuedBy;
    private String issuedPlace;
    private String issuedDate;
    private String issuedPlaceName;
    private Integer numberOfChange;
    private String changedIssuedDate;
    private String cif;
    private Boolean primary;
  }
  @Data
  public static class AddressEbRes {
    private Long id;
    private String addressType;
    private String countryCode;
    private String cityCode;
    private String cityName;
    private String districtCode;
    private String districtName;
    private String wardCode;
    private String wardName;
    private String addressLine;
    private String fullAddress;
    private Boolean hktt;
  }
}
