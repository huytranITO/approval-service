package com.msb.bpm.approval.appr.model.dto.cms;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.CARD;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.LOAN;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.OVERDRAFT;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.msb.bpm.approval.appr.model.dto.cms.ApplicationCreditDTO.DefaultApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity_;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true,
        include = JsonTypeInfo.As.EXISTING_PROPERTY, property = ApplicationCreditEntity_.CREDIT_TYPE,
        defaultImpl = DefaultApplicationCreditDTO.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ApplicationCreditLoanDTO.class, name = LOAN),
    @JsonSubTypes.Type(value = ApplicationCreditOverdraftDTO.class, name = OVERDRAFT),
    @JsonSubTypes.Type(value = ApplicationCreditCardDTO.class, name = CARD)
})
public abstract class ApplicationCreditDTO {

  private Long id;

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
  @Size(max = 10)
  private String documentCode;

  @NotBlank
  @Size(max = 20)
  private String approveResult;

  @Size(max = 100)
  private String approveResultValue;

  @NotNull
  private BigDecimal loanAmount;

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  public static class DefaultApplicationCreditDTO extends ApplicationCreditDTO {
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
    @Size(max = 10)
    private String documentCode;

    @NotBlank
    @Size(max = 20)
    private String approveResult;

    @Size(max = 100)
    private String approveResultValue;

    @NotNull
    private BigDecimal loanAmount;
  }
}
