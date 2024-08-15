package com.msb.bpm.approval.appr.model.response.bpm.operation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msb.bpm.approval.appr.model.request.bpm.operation.ApplicantCollateralMapping;
import com.msb.bpm.approval.appr.model.request.bpm.operation.AssetVersion;
import com.msb.bpm.approval.appr.model.request.bpm.operation.LoanCollateralMapping;
import lombok.*;

import java.util.List;

@Getter
@Setter
@With
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class DataBpmOperationResponse {
    private List<AssetVersion> collateralCommonRequests;
    private List<LoanCollateralMapping> loanCollateralMappings;
    private List<ApplicantCollateralMapping> applicantCollateralMappings;
}
