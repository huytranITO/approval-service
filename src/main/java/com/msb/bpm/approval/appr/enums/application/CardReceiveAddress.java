package com.msb.bpm.approval.appr.enums.application;

import java.util.Arrays;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 21/6/2023, Wednesday
 **/
public enum CardReceiveAddress {
  V001,   // Địa chỉ cư trú hiện tại
  V002,   // Địa chỉ cơ quan công tác
  V003,   // Tại PGD đăng ký thẻ
  V004;   // Khác

  public static boolean isRequired(String cardReceiveAddress) {
    return Arrays.asList(V001.name(), V002.name(), V004.name()).contains(cardReceiveAddress);
  }
}
