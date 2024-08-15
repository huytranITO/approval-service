package com.msb.bpm.approval.appr.enums.asset;

import lombok.Getter;

@Getter
public enum ActionType {
  COPY("COPY");


  private final String value;

  ActionType(String value) {
    this.value = value;
  }
}
