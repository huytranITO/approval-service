package com.msb.bpm.approval.appr.model.dto.cbt;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

@Data
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetApplicationDataDTO {
  private CustomerDataDTO customerInfo;
  private List<CustomerDataDTO> relationshipInfo;
  private List<EnterpriseDataDTO> enterpriseInfo;
  private List<AssetDataDTO> assetInfo;
  private ApplicationDataDTO applicationInfo;
}
