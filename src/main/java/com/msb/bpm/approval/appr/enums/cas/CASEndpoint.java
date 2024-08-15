package com.msb.bpm.approval.appr.enums.cas;

import lombok.Getter;

@Getter
public enum CASEndpoint {

  GET_CAS_SCORE("get-cas-score"),
  ;

  private final String value;

  CASEndpoint(String value) {
    this.value = value;
  }
}
