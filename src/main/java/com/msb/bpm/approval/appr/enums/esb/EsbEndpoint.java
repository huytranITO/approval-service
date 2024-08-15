package com.msb.bpm.approval.appr.enums.esb;

import lombok.Getter;

@Getter
public enum EsbEndpoint {

  GET_ESB_ACCOUNT("get-esb-account"),
  ;
  private final String value;

  EsbEndpoint(String value) {
    this.value = value;
  }
}
