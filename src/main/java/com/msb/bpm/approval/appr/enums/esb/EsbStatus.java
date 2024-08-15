package com.msb.bpm.approval.appr.enums.esb;

import lombok.Getter;

@Getter
public enum EsbStatus {
  ACTIVE("ACTIVE"),
  NEW_TODAY("NEW TODAY"),
  DORMANT("DORMANT"),
  ;
  private final String value;

  EsbStatus(String value) {
    this.value = value;
  }
}
