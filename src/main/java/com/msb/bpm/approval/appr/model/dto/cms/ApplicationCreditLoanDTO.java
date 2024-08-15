package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        field = "principalPayPeriod",
        fieldDependOns = "debtPayMethod"
    ),
    @CustomValidationFieldDependOn(
        field = "principalPayUnit",
        fieldDependOns = "debtPayMethod"
    ),
    @CustomValidationFieldDependOn(
        field = "interestPayPeriod",
        fieldDependOns = "debtPayMethod"
    ),
    @CustomValidationFieldDependOn(
        field = "interestPayUnit",
        fieldDependOns = "debtPayMethod"
    )
})
public class ApplicationCreditLoanDTO extends ApplicationCreditDTO {

  private Long appCreditLoanId;

  @NotBlank
  @Size(max = 255)
  private String productCode;

  @NotBlank
  @Size(max = 250)
  private String productName;

  private boolean payback;

  @NotNull
  @Size(max = 10)
  private String loanPurpose;

  @Size(max = 100)
  private String loanPurposeValue;

  @Size(max = 2000)
  private String loanPurposeExplanation;

  @NotNull
  private Integer loanPeriod;

  private Integer kunnPeriod;

  private Integer originalPeriod;

  @NotNull
  private BigDecimal totalCapital;

  @NotNull
  private BigDecimal equityCapital;

  @NotNull
  private Integer ltd;

  @NotBlank
  @Size(max = 10)
  private String creditForm;

  @Size(max = 100)
  private String creditFormValue;

  @Size(max = 10)
  private String disburseFrequency;

  @Size(max = 100)
  private String disburseFrequencyValue;

  @NotBlank
  @Size(max = 10)
  private String debtPayMethod;

  @Size(max = 100)
  private String debtPayMethodValue;

  @NotBlank
  @Size(max = 10)
  private String disburseMethod;

  @Size(max = 100)
  private String disburseMethodValue;

  @Size(max = 2000)
  private String disburseMethodExplanation;

  // Require neu debtPayMethod = OTHER
  private Integer principalPayPeriod;

  // Require neu debtPayMethod = OTHER
  private String principalPayUnit;
  private String principalPayUnitValue;

  // Require neu debtPayMethod = OTHER
  private Integer interestPayPeriod;

  // Require neu debtPayMethod = OTHER
  private String interestPayUnit;
  private String interestPayUnitValue;
}
