package com.msb.bpm.approval.appr.enums.formtemplate;

import lombok.Data;
import lombok.Getter;

@Getter
public enum SegmentTypeEum {
  V003("Khách hàng cá nhân"),
  V004("Khách hàng doanh nghiệp");

  private final String type;

  SegmentTypeEum(String s) {
    this.type = s;
  }
}
