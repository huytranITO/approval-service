package com.msb.bpm.approval.appr.enums.esb;

import lombok.Getter;

@Getter
public enum EsbAccountType {

  TYPE_01("01"),
  TYPE_11("11"),
  TYPE_68("68"),
  TYPE_79("79"),
  TYPE_86("86"),
  TYPE_88("88"),
  ;
  private final String value;

  EsbAccountType(String value) {
    this.value = value;
  }
}
