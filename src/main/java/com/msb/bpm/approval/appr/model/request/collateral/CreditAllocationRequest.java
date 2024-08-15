package com.msb.bpm.approval.appr.model.request.collateral;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CreditAllocationRequest {
    private Long id;
    private Long creditId;
    private String creditType;
    private String creditTypeValue;
    private Float percentValue;
    private String createdBy;
    private LocalDateTime createdAt;
}
