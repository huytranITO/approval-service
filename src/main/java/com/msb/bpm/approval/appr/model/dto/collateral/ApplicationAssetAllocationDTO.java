package com.msb.bpm.approval.appr.model.dto.collateral;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ApplicationAssetAllocationDTO {
    @NotNull
    private Long assetId;

    @Valid
    private List<CreditAllocationDTO> creditAllocations;
}
