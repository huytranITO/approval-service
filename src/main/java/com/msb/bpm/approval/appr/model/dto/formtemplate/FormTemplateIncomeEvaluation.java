package com.msb.bpm.approval.appr.model.dto.formtemplate;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormTemplateIncomeEvaluation {
  private String id;
  private String totalOutstandingDebt = "null";
  private String estimatedIncome = "null";
  private String appraise = "null";
  private String totalAccumulatedAssetValue = "null";
  private List<FormTemplateTotalAssetIncome> totalAssetIncomes;
}
