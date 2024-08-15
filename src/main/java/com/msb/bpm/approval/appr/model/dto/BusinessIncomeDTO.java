package com.msb.bpm.approval.appr.model.dto;

import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        message = "{javax.validation.constraints.NotNull.message}",
        field = "capitalContributionRate",
        fieldDependOns = {"incomeType"}
    ),
//    @CustomValidationFieldDependOn(
//        field = "address",
//        fieldNeedValidate = "addressLine"
//    ),
    @CustomValidationFieldDependOn(
        field = "businessRegistrationNumber",
        fieldDependOns = "incomeType"
    )
})
public class BusinessIncomeDTO extends BaseIncomeDTO {

//  @NotBlank
  @Size(max = 100)
  private String businessRegistrationNumber;

  @NotBlank
  @Size(max = 255)
  private String companyName;

  @NotBlank
  @Size(max = 250)
  private String mainBusinessSector;

  private Double capitalContributionRate;

  @Size(max = 10)
  private String businessPlaceOwnership;

  @Size(max = 100)
  private String businessPlaceOwnershipValue;

//  @NotBlank
  @Size(max = 2000)
  private String productionProcess;

  @Size(max = 2000)
  private String recordMethod;

//  @NotBlank
  @Size(max = 2000)
  private String input;

//  @NotBlank
  @Size(max = 2000)
  private String output;

//  @NotBlank
  @Size(max = 2000)
  private String businessScale;

//  @NotBlank
  @Size(max = 2000)
  private String inventory;

//  @NotBlank
  @Size(max = 10)
  private String evaluationPeriod;

  @Size(max = 100)
  private String evaluationPeriodValue;

//  @NotNull
  private BigDecimal incomeMonthly;

//  @NotNull
  private BigDecimal expenseMonthly;

//  @NotNull
  private BigDecimal profitMonthly;

//  @NotNull
  private Double profitMargin;

  @Size(max = 2000)
  private String evaluateResult;

  @Digits(integer = 10, fraction = 0)
  private Integer businessExperience;

  @NotNull
  @Valid
  private AddressDTO address;

  private String ldpBusinessId;

  private Long businessRefCustomerId;
}