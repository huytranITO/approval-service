package com.msb.bpm.approval.appr.enums.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DistributionForm {
  DF1("V001", "DF1"),
  DF2("V002", "DF2");

  private final String code;
  private final String text;
}
