package com.msb.bpm.approval.appr.model.dto.collateral;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreditAssetDTO {
    private List<Long> assetIds;
}
