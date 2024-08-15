package com.msb.bpm.approval.appr.model.dto.asset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetInfoDTO implements Serializable {

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

}