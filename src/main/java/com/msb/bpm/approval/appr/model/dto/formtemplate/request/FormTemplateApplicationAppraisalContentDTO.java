package com.msb.bpm.approval.appr.model.dto.formtemplate.request;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormTemplateApplicationAppraisalContentDTO {

  private Long id;

  private String contentType;

  private String criteriaGroup;

  private String criteriaGroupValue;

  private String detail;

  private String detailValue;

  private String regulation;

  private String managementMeasures;

  private String authorization;

  private Integer orderDisplay;
}
