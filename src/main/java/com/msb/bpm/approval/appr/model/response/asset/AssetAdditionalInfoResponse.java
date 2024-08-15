package com.msb.bpm.approval.appr.model.response.asset;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
public class AssetAdditionalInfoResponse {

  private RealEstateInfoResponse realEstateInfo;

  private TransportationInfoResponse transportationInfo;

  private StockInfoResponse stockInfo;

  private LegalDocumentInfoResponse legalDocumentInfo;

  private CollateralAssetInfoResponse collateralAssetInfo;

  private LaborContractInfoResponse laborContractInfo;

  private OtherInfoResponse otherInfo;

  private ValuationInfoResponse valuationInfo;

  private List<OwnerInfoDTO> ownerInfo = new ArrayList<>();

  private Object components;

  private Object generalObligations;

  private Object customerLoans;

  private Object propertyRightInfo;
}
