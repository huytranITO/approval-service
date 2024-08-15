package com.msb.bpm.approval.appr.model.request.rule;


import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

/*
* @author: BaoNV2
* @since: 15/5/2023 1:18 PM
* @description:
* @update:
*
* */
@With
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RuleRequest {
  private String approvalType;
  private String customerType;
  private String processFlow;
  private String submissionType;
  private String status;
  private String approveResult;
  private String proposalApprovalPosition;
  private String loanApprovalPosition;
  private Boolean riskType;
  private Integer applicationAuthorityLevel;
  private Integer userAuthorityLevel;
  private String authorityLevel;

  private List<String> userAuthorityCode;
  private String distributionFormCode;

  private ProcessingRole givebackRole;
  private String eventCode;
  private String isApprovalLevel;
}
