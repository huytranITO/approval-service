package com.msb.bpm.approval.appr.model.response.cbt;

import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerAndRelationPersonDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.response.asset.ListAssetResponse;
import java.util.Set;
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
public class SearchByRelationsResponse {
  private CustomerAndRelationPersonDTO customerAndRelation;
  private Set<ApplicationIncomeDTO> incomes;
  private ListAssetResponse assetInfo;
  private Set<CustomerDTO> enterpriseInfo;
}
