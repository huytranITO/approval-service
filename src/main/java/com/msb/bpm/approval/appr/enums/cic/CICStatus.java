package com.msb.bpm.approval.appr.enums.cic;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CICStatus {
  INFORMATION_IS_BLANK(0),

  WAITING_RESPONSE(5),
  HAVE_DATA(6),
  DO_NOT_HAVE_INFORMATION(7),
  CIC_CODE_NOT_EXIST(8),
  SYNC_ERROR(9),
  VIOLATE_CIC_RULE(11),
  CIC_DID_NOT_RECEIVE(12),
  ERROR(13),
  REQUEST_FAIL(99),
  ;

  public static final List<CICStatus> CIC_PRIORITY_ORDER = Arrays.asList(
      ERROR,
      CIC_DID_NOT_RECEIVE,
      VIOLATE_CIC_RULE,
      SYNC_ERROR,
      WAITING_RESPONSE,
      HAVE_DATA,
      DO_NOT_HAVE_INFORMATION,
      CIC_CODE_NOT_EXIST,
      INFORMATION_IS_BLANK
  );

  private final Integer status;

  public static boolean isDoNotHaveData(Integer status) {
    return equal(status, DO_NOT_HAVE_INFORMATION);
  }

  public static boolean isDoNotHaveData(String status) {
    return equal(status, DO_NOT_HAVE_INFORMATION);
  }

  public static boolean isCICCodeNotExist(Integer status) {
    return equal(status, CIC_CODE_NOT_EXIST);
  }

  public static boolean isCICCodeNotExist(String status) {
    return equal(status, CIC_CODE_NOT_EXIST);
  }

  public static boolean isHaveData(Integer status) {
    return equal(status, HAVE_DATA);
  }

  public static boolean isHaveData(String status) {
    return equal(status, HAVE_DATA);
  }

  public static int comparePriority(String status, String status1) {
    if (status == null && status1 == null) {
      return 0;
    }

    if (status == null) {
      return -1;
    }

    if (status1 == null) {
      return 1;
    }

    if (status.equals(status1)) {
      return 0;
    }

    for (CICStatus cicStatus : CIC_PRIORITY_ORDER) {
      if (equal(status, cicStatus)) {
        return 1;
      }
      if (equal(status1, cicStatus)) {
        return -1;
      }
    }
    return 0;
  }

  private static boolean equal(String status, CICStatus cicStatus) {
    return status != null
        && cicStatus != null
        && cicStatus.getStatus().toString().equals(status);
  }

  public static boolean equal(Integer status, CICStatus cicStatus) {
    return status != null
        && cicStatus != null
        && cicStatus.getStatus().equals(status);
  }
}
