package com.msb.bpm.approval.appr.model.dto.collateral;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreditAllocationDTO {
    private Long id;

    @NotNull
    private Long creditId;

    @NotNull
    @NotEmpty
    private String creditType;

    private String creditTypeValue;

    private Float percentValue;
}
