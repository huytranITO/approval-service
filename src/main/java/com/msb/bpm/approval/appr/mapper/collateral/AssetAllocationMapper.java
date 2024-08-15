package com.msb.bpm.approval.appr.mapper.collateral;

import com.msb.bpm.approval.appr.model.dto.collateral.ApplicationAssetAllocationDTO;
import com.msb.bpm.approval.appr.model.request.collateral.AssetAllocationRequest;
import com.msb.bpm.approval.appr.model.response.collateral.AssetAllocationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface AssetAllocationMapper {
    AssetAllocationMapper INSTANCE = Mappers.getMapper(AssetAllocationMapper.class);

    AssetAllocationRequest toAssetAllocationRequest(ApplicationAssetAllocationDTO dto);

    Set<AssetAllocationRequest> toAssetAllocationRequests(Set<ApplicationAssetAllocationDTO> allocationDTOS);

    ApplicationAssetAllocationDTO toAssetAllocation(AssetAllocationResponse response);
    Set<ApplicationAssetAllocationDTO> toAssetAllocationDTO(Set<AssetAllocationResponse> response);
}
