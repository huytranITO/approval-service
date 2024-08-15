package com.msb.bpm.approval.appr.model.response.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchCustomerV3Response {
  private List<CustomersEBResponse> customers;
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CustomersEBResponse {
    private CustomerEbRes customer;
    private Set<IdentityDocumentsEbRes> identityDocuments;
    private Set<AddressEbRes> addresses;
  }
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate issuedDate;
    private String issuedBy;
    private String cif;
    private String issuedPlace;
  }
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class IdentityDocumentsEbRes {
    private Long id;
    private String identityNumber;
    private String type;
    private String issuedBy;
    private String issuedPlace;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate issuedDate;
    private String issuedPlaceName;
    private Integer numberOfChange;
    private String changedIssuedDate;
    private String cif;
    private Boolean primary;
  }
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
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
