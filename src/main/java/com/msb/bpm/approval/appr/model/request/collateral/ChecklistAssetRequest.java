package com.msb.bpm.approval.appr.model.request.collateral;

import com.msb.bpm.approval.appr.model.response.collateral.AssetRuleChecklistResponse;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ChecklistAssetRequest {
    @NotNull
    private String bpmId;
    private List<AssetRuleChecklistResponse> assetInfoRequests;
}
