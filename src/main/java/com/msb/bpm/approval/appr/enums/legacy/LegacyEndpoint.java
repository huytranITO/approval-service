package com.msb.bpm.approval.appr.enums.legacy;

import lombok.Getter;

@Getter

public enum LegacyEndpoint {

  GET_CREDIT_RATING_RB("get-credit-rating-rb"),
  ;

  private final String value;

  LegacyEndpoint(String value) {
    this.value = value;
  }
}
