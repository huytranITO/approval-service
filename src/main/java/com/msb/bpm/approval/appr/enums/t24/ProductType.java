package com.msb.bpm.approval.appr.enums.t24;

import lombok.Getter;

@Getter
public enum ProductType {
  RB_CA("RB.CA"),
  ;
  private final String value;

  ProductType(String value) {
    this.value = value;
  }
}
