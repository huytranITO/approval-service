package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnsecuredLoanDTO extends ApplicationCreditCommonDTO {
  private BigDecimal totalCapital;

  private Integer loanPeriod;

  @NotBlank
  @Size(max = 10)
  private String loanPurpose;

  @NotBlank
  @Size(max = 4)
  private String creditForm;
}