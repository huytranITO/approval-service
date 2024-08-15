package com.msb.bpm.approval.appr.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
//@CustomValidationFieldDependOn(
//    field = "address",
//    fieldNeedValidate = "addressLine"
//)
public class SalaryIncomeDTO extends BaseIncomeDTO {

  @Size(max = 20)
  private String taxCode;

  @Size(max = 20)
  private String socialInsuranceCode;

//  @NotBlank
  @Size(max = 10)
  private String rankType;

  @Size(max = 100)
  private String rankTypeValue;

//  @NotBlank
  @Size(max = 10)
  private String kpiRating;

  @Size(max = 100)
  private String kpiRatingValue;

  @NotBlank
  @Size(max = 10)
  private String payType;

  @Size(max = 100)
  private String payTypeValue;

  @NotBlank
  @Size(max = 10)
  private String laborType;

  @Size(max = 100)
  private String laborTypeValue;

  @Size(max = 100)
  private String businessRegistrationNumber;

  @Size(max = 250)
  private String groupOfWorking;

  @NotBlank
  @Size(max = 255)
  private String companyName;

  @NotBlank
  @Size(max = 250)
  private String position;

  private LocalDate startWorkingDay;

  @Size(max = 2000)
  private String explanation;

  @NotNull
  @Valid
  private AddressDTO address;

  private String ldpSalaryId;

  @Size(max = 20)
  private String phoneWork;
}
