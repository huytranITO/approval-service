package com.msb.bpm.approval.appr.enums.cic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
@AllArgsConstructor
public enum CICProductCode {
  TNPN2("F5"),
  TSBD("F6"),
  ;

  private final String code;

  public static boolean isTSBD(String code) {
    return equal(code, TSBD);
  }

  private static boolean equal(String code, CICProductCode cicProductCode) {
    return StringUtils.hasText(code)
        && cicProductCode != null
        && cicProductCode.getCode().equals(code);
  }
}
