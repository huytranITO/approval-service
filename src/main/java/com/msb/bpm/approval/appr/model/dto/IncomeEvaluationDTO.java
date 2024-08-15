package com.msb.bpm.approval.appr.model.dto;

import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeEvaluationDTO {

  private Long id;

  @DecimalMin(value = "0")
  @Digits(integer = 20, fraction = 0)
  private BigDecimal totalOutstandingDebt;

  @DecimalMin(value = "0", inclusive = false)
  @Digits(integer = 20, fraction = 0)
  private BigDecimal estimatedIncome;

  @Size(max = 1000)
  private String appraise;

  @DecimalMin(value = "0")
  @Digits(integer = 20, fraction = 0)
  private BigDecimal totalAccumulatedAssetValue;

  @Valid
  private List<TotalAssetIncomeDTO> totalAssetIncomes;
}
