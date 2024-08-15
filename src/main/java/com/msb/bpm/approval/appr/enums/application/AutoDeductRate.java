package com.msb.bpm.approval.appr.enums.application;

import java.util.Arrays;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 22/6/2023, Thursday
 **/
public enum AutoDeductRate {
  V001,     // Không trích nợ

  V002,     // Tối thiểu

  V003;     // Toàn bộ

  public static boolean isDeduct(String autoDeductRate) {
    return Arrays.asList(V002.name(), V003.name()).contains(autoDeductRate);
  }
}
