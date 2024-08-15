package com.msb.bpm.approval.appr.enums.cic;

import lombok.Getter;

@Getter
public enum ResponseCodeCIC {

  SUCCESS(0),
  INVALID(-1),
  ;

  private final int code;

  ResponseCodeCIC(int code) {
    this.code = code;
  }

  public static boolean isSuccess(int code) {
    return equalCode(code, SUCCESS);
  }

  public static boolean isInvalid(int code) {
    return equalCode(code, INVALID);
  }

  public static boolean equalCode(int code, ResponseCodeCIC responseCodeCIC) {
    return responseCodeCIC != null && responseCodeCIC.getCode() == code;
  }
}
