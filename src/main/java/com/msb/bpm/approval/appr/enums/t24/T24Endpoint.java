package com.msb.bpm.approval.appr.enums.t24;

import lombok.Getter;

@Getter
public enum T24Endpoint {

  GET_T24_ACCOUNT("get-t24-account"),
  ;
  private final String value;

  T24Endpoint(String value) {
    this.value = value;
  }
}
