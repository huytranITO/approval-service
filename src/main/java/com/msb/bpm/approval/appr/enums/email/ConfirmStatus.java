package com.msb.bpm.approval.appr.enums.email;

import lombok.Getter;

@Getter
public enum ConfirmStatus {
  CONFIRM_STATUS("Đồng ý"),
  REJECT_STATUS("Từ chối"),
  EXPIRED_STATUS("Quá hạn phản hồi hồ sơ"),
  ;

  private final String value;

  ConfirmStatus(String value) {
    this.value = value;
  }
}
