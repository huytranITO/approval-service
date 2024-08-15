package com.msb.bpm.approval.appr.enums.rule;

import lombok.Getter;

@Getter
public enum RuleEndpoint {
  SEARCH_CODE("search-code")
      ;

  private final String value;

  RuleEndpoint(String value) {
    this.value = value;
  }
}
