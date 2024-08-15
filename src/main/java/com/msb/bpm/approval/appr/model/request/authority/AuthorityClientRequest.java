package com.msb.bpm.approval.appr.model.request.authority;

import com.msb.bpm.approval.appr.enums.application.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.Set;

@With
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthorityClientRequest {
    private CustomerType customerType;

    private String submissionPurpose;

    private String customerGroup;

    private Boolean creditRiskType;

    private String approvalFlow;

    private String segmentCustomer;

    private List<String> riskGroup;

    private Set<String> authorization;

    private String applicationId;

    private Integer version;

    private double loanLimitTotal;

    private double loanLimitWithoutProductTotal;

    private double loanTotalCollateral;

    private double total;

    private String requestId;

}