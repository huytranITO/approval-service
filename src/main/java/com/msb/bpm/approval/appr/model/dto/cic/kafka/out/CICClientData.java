package com.msb.bpm.approval.appr.model.dto.cic.kafka.out;

import com.msb.bpm.approval.appr.model.dto.cic.FieldName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CICClientData {

  @FieldName("TT")
  private String status;

  @FieldName("CLIENT_QUESTION_ID")
  private String clientQuestionId;

  /**
   * example: 2024-01-30 16:36:18.0
   */
  @FieldName("NGAYTL")
  private String responseDate;

  @FieldName("NOIDUNG")
  private String xmlContent;

}
