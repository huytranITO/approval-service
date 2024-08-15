package com.msb.bpm.approval.appr.enums.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RmCommitStatus {

  COMMIT(true),
  NO_COMMIT(false);

  private final boolean status;
}
