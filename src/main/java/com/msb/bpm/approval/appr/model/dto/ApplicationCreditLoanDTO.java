package com.msb.bpm.approval.appr.model.dto;

import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import com.msb.bpm.approval.appr.validator.CustomValidationNumberCompare;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

@CustomValidationNumberCompare.List({
    @CustomValidationNumberCompare(
        message = "totalCapital {custom.validation.constraints.GreaterThan.message} loanAmount",
        fieldCompare1 = "totalCapital",
        fieldCompare2 = "loanAmount",
        operator = ">="
    ),
    @CustomValidationNumberCompare(
        message = "loanAmount {custom.validation.constraints.LessThan.message} totalCapital",
        fieldCompare1 = "loanAmount",
        fieldCompare2 = "totalCapital",
        operator = "<="
    ),
    @CustomValidationNumberCompare(
        message = "kunnPeriod {custom.validation.constraints.LessThan.message} loanPeriod",
        fieldCompare1 = "kunnPeriod",
        fieldCompare2 = "loanPeriod",
        operator = "<="
    ),
    @CustomValidationNumberCompare(
        message = "originalPeriod {custom.validation.constraints.LessThan.message} loanPeriod",
        fieldCompare1 = "originalPeriod",
        fieldCompare2 = "loanPeriod",
        operator = "<="
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

  @NotBlank
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

  // Số acf
  private String acfNo;

  // Số tài khoản
  private String accountNo;

  // Trạng thái từ bpm vận hành
  private String status;

  // Mã sản phẩm chi tiết
  @NotBlank
  @Size(max = 500)
  private String productInfoCode;

  // Tên sản phẩm chi tiết
  @NotBlank
  @Size(max = 500)
  private String productInfoName;
}
