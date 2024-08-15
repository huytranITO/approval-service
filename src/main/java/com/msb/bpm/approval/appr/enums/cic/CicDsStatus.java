package com.msb.bpm.approval.appr.enums.cic;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum CicDsStatus {

  INFORMATION_IS_BLANK(0, "Không có mã CIC", 10),
  WAITING_RESPONSE(5, "Đang chờ trả lời", 6),
  HAVE_DATA(6, "Có dữ liệu (Thành công)", 7),
  DO_NOT_HAVE_INFORMATION(7, "Khách có mã CIC, không có thông tin", 8),
  CIC_CODE_NOT_EXIST(8, "Khách không có mã CIC, không có thông tin", 9),
  SYNC_ERROR(9, "Lỗi đồng bộ", 5),
  VIOLATE_CIC_RULE(11, "Vi phạm rule CIC", 3),
  CIC_DID_NOT_RECEIVE(12, "Tra cứu lỗi", 2),
  ERROR(13, "Lỗi đồng bộ", 1),
  REQUEST_FAIL(99, "Tra cứu lỗi", 4),
  ;

  private final Integer status;
  private final String description;
  private final Integer priority; // Thứ tự ưu tiên 13 → 12 → 11 → 99 → 9 → 5 → 6 → 7 → 8 → 0

  public static CicDsStatus findByStatus(int status) {
    return Arrays.stream(CicDsStatus.values()).filter(
        v -> v.getStatus().equals(status)).findFirst().orElse(null);
  }

  public static String getStatusDesc(Integer status) {
    if (status == null) {
      return null;
    }

    CicDsStatus stt = Arrays.stream(CicDsStatus.values()).filter(
        v -> v.getStatus().equals(status)).findFirst().orElse(null);
    return stt == null ? null : stt.getDescription();
  }

  private static Set<Integer> PDF_STATUS = new HashSet<>(Arrays.asList(
      HAVE_DATA.status,
      DO_NOT_HAVE_INFORMATION.status,
      CIC_CODE_NOT_EXIST.status));

  public static boolean hasPdfData(Integer status) {
    return status != null
        && PDF_STATUS.contains(status);
  }

  public boolean hasPdfData() {
    return Arrays.asList(
        HAVE_DATA,
        DO_NOT_HAVE_INFORMATION,
        CIC_CODE_NOT_EXIST).contains(this);
  }

  public static boolean hasData(Integer status) {
    return HAVE_DATA.getStatus().equals(status);
  }

  public static boolean equal(Integer status, CicDsStatus cicStatus) {
    return cicStatus != null && cicStatus.getStatus().equals(status);
  }
}
