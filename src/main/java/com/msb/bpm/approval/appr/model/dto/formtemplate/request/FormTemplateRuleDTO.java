package com.msb.bpm.approval.appr.model.dto.formtemplate.request;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormTemplateRuleDTO implements Serializable {

  private static final long serialVersionUID = 8215477069333658521L;

  public String segmentType;

  public String submissionPurpose;
  public String currentStep;
  public String nextStatus;
  public String customerGroup;
  public String authorityLevel;
}
