package com.msb.bpm.approval.appr.enums.application;

import lombok.Getter;

@Getter
public enum ConversionMethod {
  TOTAL_ASSET_METHOD("V009"),
  ;
  private final String value;
  ConversionMethod(String value) {
    this.value = value;
  }
}
