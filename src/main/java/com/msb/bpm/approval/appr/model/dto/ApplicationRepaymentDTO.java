package com.msb.bpm.approval.appr.model.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRepaymentDTO {

  private BigDecimal totalIncome;

  @NotNull
  private BigDecimal totalRepay;

  private Double dti;

  private Double dsr;

  private BigDecimal mue;

  @NotBlank
  @Size(max = 2000)
  private String evaluate;
}
