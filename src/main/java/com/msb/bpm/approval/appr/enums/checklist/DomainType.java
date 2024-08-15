package com.msb.bpm.approval.appr.enums.checklist;

import org.springframework.util.StringUtils;

public enum DomainType {

  APPLICATION,
  CUSTOMER,
  COLLATERAL,
  INCOME,
  ;

  public static boolean isCustomer(String type) {
    return equal(type, CUSTOMER);
  }

  private static boolean equal(String type, DomainType domainType) {
    return StringUtils.hasText(type)
        && domainType != null
        && domainType.toString().equals(type);
  }
}
