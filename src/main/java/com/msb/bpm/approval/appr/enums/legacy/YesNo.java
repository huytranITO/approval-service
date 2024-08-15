package com.msb.bpm.approval.appr.enums.legacy;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum YesNo {
  YES("Y"),
  NO("N");

  private final String value;

  YesNo(String value) {
    this.value = value;
  }

  public static boolean isYes(String value) {
    return equalValue(value, YES);
  }

  private static boolean equalValue(String value, YesNo yesNo) {
    if (!StringUtils.hasText(value)
        || yesNo == null) {
      return false;
    }

    return yesNo.value.equals(value);
  }
}
