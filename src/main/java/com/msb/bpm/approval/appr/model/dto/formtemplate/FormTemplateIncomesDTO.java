package com.msb.bpm.approval.appr.model.dto.formtemplate;

import java.io.Serializable;
import java.util.ArrayList;import java.util.List;import liquibase.pro.packaged.A;import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormTemplateIncomesDTO implements Serializable {

  private static final long serialVersionUID=-8519053677997425378L;

  public String id;
  private String incomeRecognitionMethod = "null";
  private String incomeRecognitionMethodValue = "null";
  private String recognizedIncome = "null";
  private String exchangeRecognizedIncome = "null";
  private String actuallyRecognizedIncome = "null";
  private String conversionMethod = "null";
  private String conversionMethodValue = "null";
  private String conversionDetail = "null";
  private String explanation = "null";
  private String currency = "null";
  private List<FormTemplateIncomeItem> incomeItems = new ArrayList<>();
  private FormTemplateIncomeEvaluation incomeEvaluation;
  private boolean checkTotalAssetMethod = false;
}
