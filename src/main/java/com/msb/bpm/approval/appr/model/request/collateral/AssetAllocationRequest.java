package com.msb.bpm.approval.appr.model.request.collateral;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AssetAllocationRequest {
    private Long assetId;
    private List<CreditAllocationRequest> creditAllocations;
}
