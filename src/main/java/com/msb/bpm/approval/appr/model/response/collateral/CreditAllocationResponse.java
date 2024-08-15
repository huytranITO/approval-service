package com.msb.bpm.approval.appr.model.response.collateral;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditAllocationResponse {
    private Long id;
    private Long creditId;
    private String creditType;
    private String creditTypeValue;
    private Float percentValue;
}
