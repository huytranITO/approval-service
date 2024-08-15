package com.msb.bpm.approval.appr.model.request.bpm.operation;


import lombok.*;

import java.util.List;

@With
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SynApplicationRequest {
  private String businessType;
  private String channel = "BPM_RISK";
  private String assignee;
  private String requestCode;
  private String approvalProcessId;
  private String approvalBpmProcessDefinitionId;
  private String approvalFromTaskDefinitionKey;
  private String approvalFromTaskId;
  private String businessUnit;
  private String processName;
  private List<String> approvalOrganizationCodes;
  private String approvalAt;
  private String approvalNum;
  private List<LoanInfo> loans;
  private List<ApplicantInfo> applicants;
  private List<Collateral> collaterals;
  private List<FileInfo> files;
  private List<Long> approvalConditionIds;
  // For Collateral asset
  private List<AssetVersion> collateralCommonRequests;
  private List<LoanCollateralMapping> loanCollateralMappings;
  private List<ApplicantCollateralMapping> applicantCollateralMappings;
  private String referenceRequestCode;
  private String segment;
}
