package com.msb.bpm.approval.appr.enums.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DocumentNoEnums {
  APPLICATION_ID("[ID hồ sơ]"),
  YEAR_APPROVED("[Năm phê duyệt]"),
  CIF("[CIF]"),
  REGION("[MB/MN]")
  ;

  private final String content;
}
