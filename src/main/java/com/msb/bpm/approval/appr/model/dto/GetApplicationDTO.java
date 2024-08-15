package com.msb.bpm.approval.appr.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

@Data
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
public class GetApplicationDTO {
  private InitializeInfoDTO initializeInfo;
  private FieldInforDTO fieldInfor;
  private DebtInfoDTO debtInfo;
  private AssetCommonInfoDTO assetInfo;
}
