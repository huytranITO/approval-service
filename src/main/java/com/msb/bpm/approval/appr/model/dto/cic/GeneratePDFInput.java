package com.msb.bpm.approval.appr.model.dto.cic;

import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePDFInput {

  private ApplicationEntity application;

  private String clientQuestionId;

  private String productCode;

  private Integer cicStatus;

}
