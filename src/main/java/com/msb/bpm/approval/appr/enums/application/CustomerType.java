package com.msb.bpm.approval.appr.enums.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
@AllArgsConstructor
public enum CustomerType {

  RB("2"),
  EB("1"),

  ;

  private String typeNumber;

  public static boolean isEB(String customerType) {
    return equal(customerType, EB);
  }

  private static boolean equal(String type, CustomerType customerType) {
    return customerType != null
        && StringUtils.hasText(type)
        && customerType.toString().equals(type);
  }
}