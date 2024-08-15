package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.enums.checklist.AssetGroup;
import com.msb.bpm.approval.appr.model.request.asset.AssetInforRequest;
import com.msb.bpm.approval.appr.model.request.asset.AssetOwnerRequest;
import com.msb.bpm.approval.appr.model.response.asset.AssetAdditionalInfoResponse;
import com.msb.bpm.approval.appr.model.response.asset.AssetDataResponse;
import com.msb.bpm.approval.appr.model.response.asset.OwnerInfoDTO;
import com.msb.bpm.approval.appr.model.response.asset.RealEstateInfoResponse;
import com.msb.bpm.approval.appr.model.response.asset.StockInfoResponse;
import com.msb.bpm.approval.appr.model.response.asset.TransportationInfoResponse;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Mapper
public interface AssetInfoMapper {

  AssetInfoMapper INSTANCE = Mappers.getMapper(AssetInfoMapper.class);

  @Mapping(target = "collateralGroup", source = "assetGroup")
  @Mapping(target = "collateralType", source = "assetType")
  @Mapping(target = "certificate", source = "assetCode")
  @Mapping(target = "idAsset", expression = "java(getObjectId(assetData.getObjectId(), assetData.getId()))")
  AssetInforRequest toAssetRequest(AssetDataResponse assetData);

  @Mapping(target = "cityCode", source = "provinceCode")
  @Mapping(target = "descriptionAsset", source = "description")
  void referenceRealEstateInfo(@MappingTarget AssetInforRequest assetInfo, RealEstateInfoResponse realEstateInfo);

  @Mapping(target = "descriptionAsset", source = "description")
  void referenceTransportationInfo(@MappingTarget AssetInforRequest assetInfo, TransportationInfoResponse transportationInfo);

  @Mapping(target = "descriptionAsset", source = "description")
  void referenceStockInfo(@MappingTarget AssetInforRequest assetInfo, StockInfoResponse stockInfo);

  @Mapping(target = "relationshipCustomer", source = "relationshipCode")
  @Mapping(target = "assetOwnerName", source = "customerName")
  AssetOwnerRequest toAssetOwner(OwnerInfoDTO ownerInfo);

  List<AssetOwnerRequest> toAssetOwners(List<OwnerInfoDTO> ownerInfo);

  default List<AssetInforRequest> convertAssetDataToAssetInfo(
      List<AssetDataResponse> assetDataResponses) {
    List<AssetInforRequest> assetInfoRequests = new ArrayList<>();
    if (!CollectionUtils.isEmpty(assetDataResponses)) {
      assetDataResponses.forEach(assetData -> {
        AssetInforRequest assetInfoRequest = AssetInfoMapper.INSTANCE.toAssetRequest(assetData);
        AssetAdditionalInfoResponse additionalInfoResponse = assetData.getAssetAdditionalInfo();

        if (AssetGroup.REAL_ESTATE.getCategoryCode()
            .equals(assetInfoRequest.getCollateralGroup())) {
          AssetInfoMapper.INSTANCE.referenceRealEstateInfo(assetInfoRequest,
              additionalInfoResponse.getRealEstateInfo());
        } else if (AssetGroup.VEHICLE.getCategoryCode()
            .equals(assetInfoRequest.getCollateralGroup())) {
          AssetInfoMapper.INSTANCE.referenceTransportationInfo(assetInfoRequest,
              additionalInfoResponse.getTransportationInfo());
        } else if (AssetGroup.VALUABLE_PAPERS.getCategoryCode()
            .equals(assetInfoRequest.getCollateralGroup())) {
          AssetInfoMapper.INSTANCE.referenceStockInfo(assetInfoRequest,
              additionalInfoResponse.getStockInfo());
        }
        // assetOwner is only one
        List<AssetOwnerRequest> assetOwner = new ArrayList<>();
        if(StringUtils.hasText(assetData.getAssetType())) {
          AssetOwnerRequest assetOwnerRequest = new AssetOwnerRequest();
          assetOwnerRequest.setRelationshipCustomer(assetData.getOwnerType());
          assetOwner.add(assetOwnerRequest);
        }
        assetInfoRequest.setAssetOwner(assetOwner);

        assetInfoRequests.add(assetInfoRequest);
      });
    }

    return assetInfoRequests;
  }

  default String getObjectId(String objectId, Long id) {
    if (org.apache.commons.lang3.StringUtils.isBlank(objectId)) {
      return id.toString();
    }
    return objectId;
  }


}
