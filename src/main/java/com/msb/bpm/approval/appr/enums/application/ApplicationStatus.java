package com.msb.bpm.approval.appr.enums.application;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationStatus {
  AS0000("0000"),
  AS0099("0099"),
  AS0001("0001"),
  AS0002("0002"),
  AS0005("0005"),
  AS0100("0100"),
  AS0003("0003"),
  AS0004("0004"),
  AS0300("0300"),
  AS0008("0008"),
  AS0006("0006"),
  AS9999("9999"),
  AS0500("0500"),
  AS0007("0007"),
  AS0605("0605"),
  AS0706("0706"),
  AS0009("0009"),
  AS0805("0805"),
  AS0010("0010"),
  AS0012("0012"),
  AS0908("0908"),
  AS0011("0011"),
  AS1009("1009"),
  AS1110("1110"),
  AS1209("1209"),
  AS4000("4000"),
  AS4001("4001"),
  AS4003("4003"),
  AS_NULL(null);


  private final String value;

  /**
   * Trạng thái hồ sơ được phép chuyển lên CBXL
   *
   * @param currentStatus String  trạng tha hiện tại của hồ sơ
   * @return boolean
   */
  public static boolean isTransferToHandlingOfficer(String currentStatus) {
    return Arrays.asList(AS0002.getValue(), AS0004.getValue(), AS0005.getValue()).contains(currentStatus);
  }

  /**
   * Trạng thái hồ sơ được phép chuyển lên CBĐP
   *
   * @param currentStatus String  trạng tha hiện tại của hồ sơ
   * @return boolean
   */
  public static boolean isTransferToCoordinator(String currentStatus) {
    return Arrays.asList(AS0002.getValue(), AS0003.getValue()).contains(currentStatus);
  }

  /**
   * Trạng thái hồ sơ gán lần đầu
   *
   * @param currentStatus String  trạng thái hiện tại của hồ sơ
   * @return  boolean
   */
  public static boolean isAssignUserReception(String currentStatus) {
    return Arrays.asList(AS0002.getValue(), AS0004.getValue()).contains(currentStatus);
  }

  /**
   * Trạng thái hồ sơ hoàn thành
   *
   * @param currentStatus String  trạng thái hiện tại của hồ sơ
   * @return boolean
   */
  public static boolean isComplete(String currentStatus) {
    return Arrays.asList(AS0099.getValue(), AS9999.getValue()).contains(currentStatus);
  }

  public static boolean isDone(String status){
    return AS9999.getValue().equalsIgnoreCase(status);
  }
}
