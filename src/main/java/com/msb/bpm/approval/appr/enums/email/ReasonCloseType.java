package com.msb.bpm.approval.appr.enums.email;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 19/7/2023, Wednesday
 **/
@Getter
@RequiredArgsConstructor
public enum ReasonCloseType {
  RS001("ĐVKD rút hồ sơ, không trình"),
  RS002("ĐVKD trình không đúng phân luồng"),
  RS003("Gửi sai hồ sơ KH/trùng lặp hồ sơ"),
  RS004("Khách hàng không có nhu cầu giải ngân"),
  RS005("Khách hàng không còn nhu cầu vay vốn"),
  RS006("Khách hàng không đồng ý đi thực địa"),
  RS007("Phê duyệt nguyên tắc"),
  RS008("Phê duyệt chủ trương"),
  RS009("Tư vấn lại hồ sơ vay cho khách hàng"),
  RS010("Xác định sai đối tượng cho vay/Chủ SH"),
  RS011("Hồ sơ từ 1-3 lỗi FTR"),
  RS012("Hồ sơ từ 4 lỗi FTR trở lên"),
  RS013("KH là đối tượng không cấp tín dụng"),
  RS014("Hồ sơ có kết luận gian lận"),
  RS015("Lý do khác"),
  ;

  private final String value;
  public static String getListReasonByCode(List<String> names) {
    List<String> lstReasons = new ArrayList<>();
    names.forEach(e -> {
      ReasonCloseType reasonCloseType = ReasonCloseType.valueOf(e);
      lstReasons.add(reasonCloseType.getValue());
    });
    return String.join(";", lstReasons);
  }
}