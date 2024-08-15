package com.msb.bpm.approval.appr.enums.regulatoryCode;

import lombok.Getter;

@Getter
public enum RegulatoryCode {
  QD_DF_006("QD_DF_006"),
  QD_RB_076("QD_RB_076"),

  ;
  private final String value;

  RegulatoryCode(String value) {
    this.value = value;
  }
}
