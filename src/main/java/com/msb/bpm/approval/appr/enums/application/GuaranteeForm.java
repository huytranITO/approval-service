package com.msb.bpm.approval.appr.enums.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 15/5/2023, Monday
 **/
@Getter
@RequiredArgsConstructor
public enum GuaranteeForm {
  COLLATERAL("V001"),
  UNSECURE("V002");

  private final String code;
}
