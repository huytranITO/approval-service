package com.msb.bpm.approval.appr.enums.application;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerExtType {
  CUSTOMER("Khách hàng"),
  CUSTOMER_RELATIONSHIP("Người liên quan"),
  ENTERPRISE("Doanh nghiệp");

  private String value;

  public static CustomerExtType get(String value) {
    return Arrays.stream(CustomerExtType.values()).filter(e -> StringUtils.equals(e.getValue(), value))
        .findFirst().orElse(null);
  }
}
