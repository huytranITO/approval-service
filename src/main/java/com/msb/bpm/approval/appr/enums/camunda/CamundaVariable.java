package com.msb.bpm.approval.appr.enums.camunda;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 10/5/2023, Wednesday
 **/
@RequiredArgsConstructor
@Getter
public enum CamundaVariable {
  STATUS("status"),
  ACTION("action"),
  TRANSFER_TO("transferTo"),
  NEXT_STEP("nextStep"),
  WORKFLOW_VERSION("workflowVersion"),
  SYNC_STATE("syncState"),
  REQUEST_CODE("requestCode"),
  END_TASK("End");

  private final String value;
}
