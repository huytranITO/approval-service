package com.msb.bpm.approval.appr.model.request.asset;

import com.msb.bpm.approval.appr.model.response.asset.AssetDataResponse;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 7/10/2023, Saturday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BPMAssetRequest implements Serializable {

  private String applicationId;
  private String businessType;
  private String actionType;
  private List<AssetDataResponse> assetData;
}
