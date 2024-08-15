package com.msb.bpm.approval.appr.model.request.authority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LimitLoanInfoRequest {
    @Min(value = 0L, message = "The value must be positive")
    private double loanProductCollateral = 0;

    @Min(value = 0L, message = "The value must be positive")
    private double otherLoanProductCollateral = 0;

    @Min(value = 0L, message = "The value must be positive")
    private double unsecureProduct = 0;

    @Min(value = 0L, message = "The value must be positive")
    private double otherUnsecureProduct = 0;
}
