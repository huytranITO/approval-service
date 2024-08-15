package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ApplicationCreditOverdraftDTO extends ApplicationCreditDTO {

  private Long appCreditOverdraftId;

  @NotBlank
  @Size(max = 255)
  private String interestRateCode;

  @NotBlank
  @Size(max = 250)
  private String productName;

  @NotBlank
  @Size(max = 10)
  private String loanPurpose;

  @Size(max = 100)
  private String loanPurposeValue;

  @NotNull
  private Integer limitSustentivePeriod;

//  @NotBlank
  @Size(max = 10)
  private String debtPayMethod;

  @Size(max = 100)
  private String debtPayMethodValue;

  @Size(max = 1000)
  private String infoAdditional;
}
