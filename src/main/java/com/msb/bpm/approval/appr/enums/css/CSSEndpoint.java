package com.msb.bpm.approval.appr.enums.css;

import lombok.Getter;

@Getter
public enum CSSEndpoint {

  GET_SCORE_RB("get-score-rb"),
  ;

  private final String value;

  CSSEndpoint(String value) {
    this.value = value;
  }
}
