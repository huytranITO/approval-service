package com.msb.bpm.approval.appr.model.dto.formtemplate;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FormTemplateApplicationIncomeDTO {

  private String id;
  private String incomeRecognitionMethod;
  private String incomeRecognitionMethodValue;
  private String recognizedIncome;
  private String conversionMethod;
  private String conversionMethodValue;
  private String explanation;
  private List<FormTemplateIncomeItem> incomeItems;

}


