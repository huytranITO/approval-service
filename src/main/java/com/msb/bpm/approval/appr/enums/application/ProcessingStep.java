package com.msb.bpm.approval.appr.enums.application;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 13/5/2023, Saturday
 **/
@Getter
@RequiredArgsConstructor
public enum ProcessingStep {

  MAKE_PROPOSAL("V001", "make_proposal"),
  APPROVE_PROPOSAL("V002", "approve_proposal"),
  TL_COORDINATOR("HT04", "tl_coordinator"),
  COORDINATOR("V005", "coordinator"),
  APPROVE_PROFILE_1("V006", "approve_profile_1"),
  APPROVE_PROFILE_2("V007", "approve_profile_2"),
  APPROVE_PROFILE_3("V008", "approve_profile_3"),
  MANAGE_PROFILES("V009", "manage_profiles"),
  COUNCIL_APPROVAL_1("V010", "council_approval_1"),
  COUNCIL_APPROVAL_2("V011", "council_approval_2"),
  COUNCIL_APPROVAL_3("V012", "council_approval_3"),
  STOP_TRANSITION("HT03", "stop_transition"),
  FLOW_COMPLETE("HT01", "flow_complete"),
  CLOSE_APPLICATION("CL01", "close_application");

  private final String code;
  private final String value;

  public static ProcessingStep get(String value) {
    return Arrays.stream(ProcessingStep.values()).filter(e -> StringUtils.equals(e.getCode(), value))
        .findFirst().orElse(null);
  }

  /**
   * Kiểm tra bước tiếp theo lấy danh sách user theo thẩm quyền
   *
   * @param nextStepCode  String Mã của bước tiếp theo trong luồng phê duyệt
   * @return  boolean
   */
  public static boolean isNextStepGetUserByAuthority(String nextStepCode) {
    return Arrays.asList(APPROVE_PROFILE_1.getCode(), APPROVE_PROFILE_2.getCode(),
            APPROVE_PROFILE_3.getCode(),
            COUNCIL_APPROVAL_1.getCode(), COUNCIL_APPROVAL_2.getCode(), COUNCIL_APPROVAL_3.getCode())
        .contains(nextStepCode);
  }

  /**
   * Kiểm tra bước tiếp theo lấy danh sách user theo roles
   *
   * @param nextStepCode  String Mã của bước tiếp theo trong luồng phê duyệt
   * @return boolean
   */
  public static boolean isNextStepGetUserByRoles(String nextStepCode) {
    return Arrays.asList(APPROVE_PROPOSAL.getCode(), MANAGE_PROFILES.getCode())
        .contains(nextStepCode);
  }

  public static boolean isNextStepGetCurrAuthority(String nextStepCode) {
    return Arrays.asList(TL_COORDINATOR.getCode(), COORDINATOR.getCode(),
        APPROVE_PROFILE_1.getCode()).contains(nextStepCode);
  }

  public static boolean isNextStepGetAuthoritiesGreaterCurrAuthority(String nextStepCode) {
    return Arrays.asList(APPROVE_PROFILE_2.getCode(), APPROVE_PROFILE_3.getCode())
        .contains(nextStepCode);
  }
}
