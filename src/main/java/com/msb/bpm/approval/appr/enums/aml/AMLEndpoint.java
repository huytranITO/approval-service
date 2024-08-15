package com.msb.bpm.approval.appr.enums.aml;

import lombok.Getter;

@Getter
public enum AMLEndpoint {
  GET_AML_INFO("get-aml-info"),
  ;

  private final String value;

  AMLEndpoint(String value) {
    this.value = value;
  }
}
