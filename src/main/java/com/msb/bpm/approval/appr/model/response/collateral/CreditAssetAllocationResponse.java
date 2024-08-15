package com.msb.bpm.approval.appr.model.response.collateral;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditAssetAllocationResponse {
    private List<AssetAllocationResponse> assetAllocations;
    private List<CreditAssetMappingResponse> assets;

}
