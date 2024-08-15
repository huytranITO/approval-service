package com.msb.bpm.approval.appr.enums.application;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 15/5/2023, Monday
 **/
public enum TNRProductCode {
  RLNSTCBNVA,
  RLNMTCBNVA,
  RLNLTCBNVA,
  RLNST3PCT,
  RLNMT3PCT,
  RLNLT3PCT;

  public static List<String> tnrProductCodes() {
    return Stream.of(TNRProductCode.values())
        .map(TNRProductCode::name)
        .collect(Collectors.toList());
  }
}
