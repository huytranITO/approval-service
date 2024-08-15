package com.msb.bpm.approval.appr.model.dto.rule;

import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 29/5/2023, Monday
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
@ToString
public class TransitionConditionDTO {

  private String approvalType;
  private String distributionFormCode;
  private String processFlow;
  private String submissionPurpose;
  private String status;
  private String customerType;
  private Integer applicationAuthorityLevel;
  private String priorityAuthority;
  private String loanApprovalPosition;
  private String proposalApprovalPosition;
  private String bpmId;
  private Boolean riskLevel;
  private Set<ApplicationCreditDTO> credits;
  private ProcessingRole givebackRole;
  private String eventCode;
  private String processingRole;
}
