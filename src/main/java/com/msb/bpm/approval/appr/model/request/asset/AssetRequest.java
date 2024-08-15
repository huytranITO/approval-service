package com.msb.bpm.approval.appr.model.request.asset;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetRequest {

  private List<AssetInforRequest> assetInfo = new ArrayList<>();

}
