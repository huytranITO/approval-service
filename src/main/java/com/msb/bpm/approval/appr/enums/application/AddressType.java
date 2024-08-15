package com.msb.bpm.approval.appr.enums.application;

import org.apache.commons.lang3.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AddressType {
  HK_THUONG_TRU("V001"), DIA_CHI_TSC("V002");

  private String value;

  public static AddressType get(String value) {
    return Arrays.stream(AddressType.values()).filter(e -> StringUtils.equals(e.getValue(), value))
        .findFirst().orElse(null);
  }
}
