package com.msb.bpm.approval.appr.model.response.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransitionResponse {
  private String stepCode;
  private String nextStep;
  private String nextRole;
  private String nextTask;
}
