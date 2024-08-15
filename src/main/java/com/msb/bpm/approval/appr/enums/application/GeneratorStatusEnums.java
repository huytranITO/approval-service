package com.msb.bpm.approval.appr.enums.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GeneratorStatusEnums {
  DONE("02"),
  PROCESSING("01"),
  DEFAULT("00");

  private final String value;
}
