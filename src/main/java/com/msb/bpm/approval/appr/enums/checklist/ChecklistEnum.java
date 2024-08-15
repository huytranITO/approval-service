package com.msb.bpm.approval.appr.enums.checklist;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum ChecklistEnum {

  CREDITWORTHINESS("0105"),
  ;

  private final String code;

  ChecklistEnum(String code) {
    this.code = code;
  }

  public static boolean isCreditworthiness(String code) {
    return equal(code, CREDITWORTHINESS);
  }

  private static boolean equal(String code, ChecklistEnum checklistEnum) {
    return StringUtils.hasText(code)
        && checklistEnum != null
        && checklistEnum.getCode().equals(code);
  }
}
