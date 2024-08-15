package com.msb.bpm.approval.appr.model.response.cic;

import com.msb.bpm.approval.appr.enums.cic.ResponseCodeCIC;
import lombok.Data;

@Data
public class CICResponse {

  private Integer code;

  private Long transactionTime;

  private Object value;

  private String message;

  private String category;

  /**
   * Có giá trị khi lỗi thực thi trong CIC
   */
  private CICStatus status;

  private String data;

  public boolean isInvalidResponse() {
    return ResponseCodeCIC.isInvalid(code);
  }

  public boolean isInternalError() {
    return status != null;
  }
}
