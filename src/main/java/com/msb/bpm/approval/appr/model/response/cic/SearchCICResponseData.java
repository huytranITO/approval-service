package com.msb.bpm.approval.appr.model.response.cic;

import lombok.Data;

@Data
public class SearchCICResponseData {

  private Integer status;

  private String h2hResponseTime;

  private String requestTime;

  private Long clientQuestionId;

  private String cicCode;

  private String uniqueId;
}
