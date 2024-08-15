package com.msb.bpm.approval.appr.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CustomerTransactionIncomeDTO {
  private Long id;

  private LocalDate transactionTime;

  @Size(max = 1000)
  @NotNull
  @NotEmpty
  private String asset;

  @NotNull
  private BigDecimal transactionValue;

  @NotNull
  private BigDecimal purchaseCost;

  @NotNull
  private BigDecimal brokerageCost;

  @NotNull
  private BigDecimal transferNameCost;

  private BigDecimal profit;

  private Integer orderDisplay;

}
