package com.msb.bpm.approval.appr.enums.esb;

import lombok.Getter;

@Getter
public enum EsbCurrencyType {
  CURRENCY_VND("VND"),
  ;
  private final String value;

  EsbCurrencyType(String value) {
    this.value = value;
  }
}
