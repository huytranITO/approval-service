package com.msb.bpm.approval.appr.enums.cas;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum CASResponseCode {

  CANNOT_AUTHEN("DS50000", "Request thiếu thông tin authen"),
  SUCCESS("CSS_000", "Thành công"),
  AUTHEN_FAILURE("CSS_002", "Lỗi xác thực"),
  MISSING_INFO("CSS_003", "Thiếu thông tin"),
  INVALID_INPUT("CSS_004", "Thông tin không hợp lệ"),
  DUPLICATE_REQUEST_TIME("CSS_005", "Trùng request time"),
  REQUEST_TIME_WRONG_DATA_FORMAT("CSS_006", "Request time sai định dạng"),
  INVALID_LENGTH("CSS_007", "Độ dài không hợp lệ"),
  NO_SCORE("CSS_011", "Chưa có điểm"),
  CLOSED_PROFILE("CSS_012", "Hồ sơ đã bị đóng"),
  ID_NOT_EXIST("CSS_013", "Id không tồn tại"),
  NOT_MATCH_REGIS_NUM("CSS_014", "Mã số doanh nghiệp không khớp"),
  NOT_MATCH_LEGAL_NO("CSS_015", "Mã số định danh của khách hàng không khớp"),
  OTHER("CSS_016", "Lỗi khác"),
  SYSTIME_ERROR("CSS_999", "Lỗi hệ thống"),
  ;

  private final String code;
  private final String description;

  CASResponseCode(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public static boolean isCode(String code, CASResponseCode cssResponseCode) {
    if (!StringUtils.hasText(code)
        || cssResponseCode == null) {
      return false;
    }
    return cssResponseCode.getCode().equals(code);
  }
}
