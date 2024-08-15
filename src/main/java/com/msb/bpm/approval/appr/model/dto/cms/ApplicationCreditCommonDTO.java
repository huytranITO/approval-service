package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationCreditCommonDTO {
  @NotBlank
  @Size(max = 10)
  private String creditType;

  @NotNull
  private BigDecimal loanAmount;

  @NotBlank
  @Size(max = 10)
  private String guaranteeForm;
}