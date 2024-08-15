package com.msb.bpm.approval.appr.model.dto.formtemplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormTemplateAssetDTO {
    private String assetGroupName = "null";
    private String assetTypeName = "null";
    private String assetName = "null";
    private String nextValuationDay = "null";
    private String assetProvinceName = "null";
    private String assetDistrictName = "null";
    private String assetWardName = "null";
    private String assetStreetNumber = "null";
    private String assetLandParcel = "null";
    private String assetLandPlot = "null";
    private String assetHouseNumber = "null";
    private String assetFloor = "null";
    private String assetSeriNumber = "null";
    private String assetMachineNumber = "null";
    private String assetPlateNumber = "null";
    private String assetPropertyDetails = "null";
    private String assetQuantity = "null";
    private String assetDistributors = "null";
    private String assetDescription = "null";
    private String assetDocName = "null";
    private String assetDocDescription = "null";
    private String assetDocValue = "null";
    private String assetDateOfIssue = "null";
    private String assetIssuedBy = "null";
    private String proposalCollateralValue = "null";
    private String valuationGuaranteed = "null";
    private String assetLtv = "null";
    private String assetRealDescription = "null";
    private String assetTransportDescription = "null";
    private String assetStockDescription = "null";
    private String assetLegalDescription = "null";
    private String assetLaborDescription = "null";
    private String assetOtherDescription = "null";
    private String assetCollateralDescription = "null";
    private String assetCode = "null";
    private String valuationDate = "null";
    private String assetStockParValue = "null";
    private String assetOwnerTypeName = "null";
    private List<FormTemplateCustomerAsset> ownerInfo =  new ArrayList<>();
}