package com.msb.bpm.approval.appr.enums.cic;

import lombok.Getter;

@Getter
public enum CICEndpoint {
  SEARCH_CODE("search-code"),
  SEARCH("search"),
  SEARCH_INTEGRATION("cicInfo"),
  ;

  private final String value;

  CICEndpoint(String value) {
    this.value = value;
  }
}
