package com.msb.bpm.approval.appr.model.response.cic;

import lombok.Data;

@Data
public class BaseResponseDto<T> {

  private Integer code;

  private String message;

  private T value;
}
