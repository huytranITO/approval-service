package com.msb.bpm.approval.appr.model.request.bpm.operation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;

@With
@NoArgsConstructor
@AllArgsConstructor
@Data
public class IncomeInfo {
  private String type;
  private String incomeRecognitionMethod;
  private BigDecimal recognizedIncome;
  private String currency;
  private String explanation;
  private Long approvalIncomeId;
}
