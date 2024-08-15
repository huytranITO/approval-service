package com.msb.bpm.approval.appr.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageCode {

  FEEDBACK_EDITING_SUCCESS("FB_01", "Chờ khách hàng phản hồi"),
  FEEDBACK_CONFIRM_SUCCESS("FB_02", "Chờ khách hàng xác nhận"),
  FEEDBACK_ERROR("FB_03", "Bạn chưa nhập thông tin phản hồi cho khách hàng(comment, hồ sơ)"),
  FEEDBACK_ERROR_WITH_NO_GREEN_TICK("FB_04",
      "Thông tin bạn vừa nhập liệu/sửa đổi ảnh hưởng đến ĐNVV. Để gửi phản hồi cho khách hàng xác nhận bạn vui lòng nhập liệu đủ thông tin tại tất cả các tab để hồ sơ được tích xanh");
  private final String code;
  private final String value;
}
