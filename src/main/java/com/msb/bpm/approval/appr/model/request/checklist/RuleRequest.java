package com.msb.bpm.approval.appr.model.request.checklist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RuleRequest {
  private Long ruleVersion;
  private String rootGroup;
  private String domainType;
  private Long domainObjectId;
  private String customerType;
  private String submissionPurpose;
  private String submissionMethod;
  private String role;
  private String customerSegment;
  private String submissionFlow;
  private String incomeType;
  private String customerRelationShip;
  private String loanPurpose;
  private String groupCode;
  private String guarantee;
  private String assetGroup;
  private String assetType;
  private String isLoanPurpose;
  private String investorInformation;
}