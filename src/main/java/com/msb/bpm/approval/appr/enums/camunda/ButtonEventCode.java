package com.msb.bpm.approval.appr.enums.camunda;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 30/5/2023, Tuesday
 **/
@Getter
@RequiredArgsConstructor
public enum ButtonEventCode {
  SUCCESS("0000"),
  REWORK("0001"),
  ALLOCATE("0002"),
  REASSIGN("0003"),
  REASSIGN_TEAM_LEAD("0004");

  private final String code;
}
