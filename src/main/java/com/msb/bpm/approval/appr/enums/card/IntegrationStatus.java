package com.msb.bpm.approval.appr.enums.card;

import lombok.Getter;

@Getter
public enum IntegrationStatus {
  NEW("Chờ tích hợp"),
  INPROGESS("Đang tích hợp"),
  SUCESSFULL("Hoàn thành"),
  ERR("Lỗi tích hợp")
  ;

  IntegrationStatus(String value) {
    this.value = value;
  }

  private final String value;

}
