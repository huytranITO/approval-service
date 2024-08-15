package com.msb.bpm.approval.appr.model.request.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetOwnerRequest {

  private String relationshipCustomer;

  private String assetOwnerName;

}
