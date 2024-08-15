package com.msb.bpm.approval.appr.model.request.checklist;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetChecklistRequest {
  private String ruleCode;
  private String requestCode;
  private String businessType;
  private String businessFlow;
  private String phaseCode;
  private Boolean isReused;
  private List<RuleRequest> listRuleRequest;
}
