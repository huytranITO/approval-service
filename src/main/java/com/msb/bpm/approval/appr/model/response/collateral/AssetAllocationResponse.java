package com.msb.bpm.approval.appr.model.response.collateral;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetAllocationResponse {
    private Long assetId;
//    private String assetGroup;
    private Set<CreditAllocationResponse> creditAllocations;
}