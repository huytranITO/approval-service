package com.msb.bpm.approval.appr.model.dto.formtemplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormTemplateTotalAssetIncome {
  private String id;
  private String assetGroup = "null";
  private String assetGroupValue = "null";
  private String baseDocument = "null";
  private String assetDescription = "null";
  private String assetValue = "null";
  private String evaluateMethod = "null";
  private String evaluateMethodValue = "null";
  private Integer orderDisplay;
}
