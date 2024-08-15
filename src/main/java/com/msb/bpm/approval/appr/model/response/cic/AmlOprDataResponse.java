package com.msb.bpm.approval.appr.model.response.cic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmlOprDataResponse {
  private Long customerId;
  private Long refCustomerId;
  private String identifierCode;
  private String queryType;
  private String resultCode;
  private String resultDescription;
  private String subject;
  private String reasonInList;
  private String startDate;
  private String endDate;
  private boolean priority;
}