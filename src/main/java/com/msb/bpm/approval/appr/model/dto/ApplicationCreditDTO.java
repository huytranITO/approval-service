package com.msb.bpm.approval.appr.model.dto;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.CARD;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.LOAN;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.OVERDRAFT;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO.DefaultApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity_;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true,
        include = JsonTypeInfo.As.EXISTING_PROPERTY, property = ApplicationCreditEntity_.CREDIT_TYPE,
        defaultImpl = DefaultApplicationCreditDTO.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ApplicationCreditLoanDTO.class, name = LOAN),
    @JsonSubTypes.Type(value = ApplicationCreditOverdraftDTO.class, name = OVERDRAFT),
    @JsonSubTypes.Type(value = ApplicationCreditCardDTO.class, name = CARD)
})
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        message = "loanAmount {custom.validation.constraints.NotExceed.message} 2.000.000.000",
        field = "loanAmount",
        fieldDependOns = "creditType"
    )
})
public abstract class ApplicationCreditDTO {

  private Long id;

  private String ldpCreditId;

  private List<Long> assets; // Tai san phan bo cho tung khoan vay

  @NotBlank
  @Size(max = 10)
  private String creditType;

  @Size(max = 100)
  private String creditTypeValue;

  @NotBlank
  @Size(max = 10)
  private String guaranteeForm;

//  @NotBlank
  @Size(max = 100)
  private String guaranteeFormValue;

  @NotBlank
  @Size(max = 45)
  private String documentCode;

  @NotBlank
  @Size(max = 20)
  private String approveResult;

  @Size(max = 100)
  private String approveResultValue;

  @NotNull
  @DecimalMin(value = "0")
  private BigDecimal loanAmount;

  private Integer orderDisplay;

  private Boolean isAllocation;

  private String idDraft;

  @NoArgsConstructor
  public static class DefaultApplicationCreditDTO extends ApplicationCreditDTO {}
}
