package com.msb.bpm.approval.appr.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreditAllocationDTO {
    @NotNull
    private String allocationCode;

    private Long id;

    @NotNull
    private Long creditId;

    @NotNull
    @NotEmpty
    private String creditType;

    @NotNull
    @NotEmpty
    private String creditTypeValue;

    @NotNull
    private Float percentValue;
}
