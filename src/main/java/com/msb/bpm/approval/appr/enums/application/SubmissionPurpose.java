package com.msb.bpm.approval.appr.enums.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubmissionPurpose {
  NEW_LEVEL("V001", "NEW_LEVEL"),
  RE_LEVELING("V002", "RE_LEVELING"),
  ADJUST("V003", "ADJUST");

  private final String code;
  private final String text;
}
