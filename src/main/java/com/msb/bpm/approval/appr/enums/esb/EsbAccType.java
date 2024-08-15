package com.msb.bpm.approval.appr.enums.esb;

import lombok.Getter;

@Getter
public enum EsbAccType {
  ACC_TYPE_D("D"),
  ;
  private final String value;

  EsbAccType(String value) {
    this.value = value;
  }
}
