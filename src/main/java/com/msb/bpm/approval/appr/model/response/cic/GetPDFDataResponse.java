package com.msb.bpm.approval.appr.model.response.cic;

import lombok.Data;

@Data
public class GetPDFDataResponse {

  private Integer code;

  private Long transactionTime;

  private String message;

  private String category;

  private Object value;

}
