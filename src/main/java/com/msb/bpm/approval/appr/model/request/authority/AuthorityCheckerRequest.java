package com.msb.bpm.approval.appr.model.request.authority;

import com.msb.bpm.approval.appr.enums.application.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@With
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthorityCheckerRequest {
    @NotNull
    private CustomerType customerType;

    @NotNull
    private String submissionPurpose;

    @NotNull
    private String customerGroup;

    @NotNull
    private Boolean creditRiskType;

    @NotNull
    private String approvalFlow;

    @NotNull
    private String segmentCustomer;

    @NotNull
    private List<String> riskGroup;

    private Set<String> authorization;

    @NotNull
    private String applicationId;

    @Valid
    private LimitLoanInfoRequest currentLimit;

    @Valid
    private LimitLoanInfoRequest preApproveLimit;

    @Valid
    private LimitLoanInfoRequest recommendLimit;


    public double getLoanLimitTotal() {
        return currentLimit.getLoanProductCollateral() + currentLimit.getOtherLoanProductCollateral() + currentLimit.getUnsecureProduct() + currentLimit.getOtherUnsecureProduct()
                + preApproveLimit.getLoanProductCollateral() + preApproveLimit.getOtherLoanProductCollateral() + preApproveLimit.getUnsecureProduct() + preApproveLimit.getOtherUnsecureProduct()
                + recommendLimit.getLoanProductCollateral() + recommendLimit.getOtherLoanProductCollateral() + recommendLimit.getUnsecureProduct() + recommendLimit.getOtherUnsecureProduct();
    }

    public double getLoanLimitWithoutProductTotal() {
        return currentLimit.getOtherLoanProductCollateral() + currentLimit.getOtherUnsecureProduct()
                + preApproveLimit.getOtherLoanProductCollateral() + preApproveLimit.getOtherUnsecureProduct()
                + recommendLimit.getOtherLoanProductCollateral() + recommendLimit.getOtherUnsecureProduct();
    }

    public double getLoanTotalCollateral() {
        return currentLimit.getUnsecureProduct() + currentLimit.getOtherUnsecureProduct()
                + preApproveLimit.getUnsecureProduct() + preApproveLimit.getOtherUnsecureProduct()
                + recommendLimit.getUnsecureProduct() + recommendLimit.getOtherUnsecureProduct();
    }

    public double getTotal() {
        return recommendLimit.getLoanProductCollateral() + recommendLimit.getOtherLoanProductCollateral() + recommendLimit.getUnsecureProduct() + recommendLimit.getOtherUnsecureProduct();
    }
}