package com.msb.bpm.approval.appr.model.response.asset;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class RealEstateInfoResponse {

  private Long id;

  private String provinceCode;

  private String provinceName;

  private String districtCode;

  private String districtName;

  private String wardCode;

  private String wardName;

  @JsonProperty("streetNumber")
  private String addressLine;

  private String houseNumber;

  private String floor;

  private String landPlot;

  private String landParcel;

  private String mapLocation;

  private String investorInformation;

  private String description;

  private String payMethod;

  private boolean buildingWork;

  private boolean futureAsset;
}
