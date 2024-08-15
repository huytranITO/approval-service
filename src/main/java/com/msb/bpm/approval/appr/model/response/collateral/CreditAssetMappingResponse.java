package com.msb.bpm.approval.appr.model.response.collateral;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditAssetMappingResponse {
    private Long credit;
    private List<Long> assets;
}
