package com.msb.bpm.approval.appr.enums.email;

import lombok.Getter;

@Getter
public enum NoticeFeedback {
  NOTICE_FEEDBACK("Thời gian phản hồi từ khách hàng đã vượt quá tổng thời lượng phản hồi tối đa theo quy định. Anh/chị vui lòng liên hệ với khách hàng để nắm rõ nhu cầu xử lý hồ sơ.")
  ;
  private final String value;

  NoticeFeedback(String value) {
    this.value = value;
  }
}
