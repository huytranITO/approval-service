package com.msb.bpm.approval.appr.model.request.asset;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetInforRequest {

  private String idAsset;

  private String collateralGroup;

  private String collateralType;

  private String certificate;

  private String cityCode;

  private String districtCode;

  private String wardCode;

  private String addressLine;

  private String houseNumber;

  private String floor;

  private String landPlot;

  private String landParcel;

  private String mapLocation;

  private String descriptionAsset;

  private List<AssetOwnerRequest> assetOwner;
}
