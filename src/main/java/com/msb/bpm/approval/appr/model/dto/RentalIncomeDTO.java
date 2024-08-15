package com.msb.bpm.approval.appr.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
public class RentalIncomeDTO extends BaseIncomeDTO {

  @NotBlank
  @Size(max = 10)
    private String assetType;

  @Size(max = 100)
  private String assetTypeValue;

//  @NotBlank
  @Size(max = 250)
  private String assetOwner;

  @Size(max = 250)
  private String renter;

//  @NotBlank
  @Size(max = 20)
  private String renterPhone;

  @NotBlank
  @Size(max = 10)
  private String rentalPurpose;

  @Size(max = 100)
  private String rentalPurposeValue;

//  @NotNull
  private BigDecimal rentalPrice;

  @Size(max = 2000)
  private String explanation;

  private String ldpRentalId;

  @NotNull
  private AddressDTO address;

  private String addressType;

  private String addressTypeValue;

  private String addressLinkId;
}
