package com.msb.bpm.approval.appr.enums.common;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LdpStatus {
  CUSTOMER_EDITED("4000"),
  WAIT_CUSTOMER_EDITING("4001"),
  WAIT_CUSTOMER_CONFIRM("4003"),
  NULL_VALUE(null);

  private final String value;
}
