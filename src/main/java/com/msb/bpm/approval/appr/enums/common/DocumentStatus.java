package com.msb.bpm.approval.appr.enums.common;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentStatus {
  ENOUGH("Đủ"), LACK("Thiếu");

  private final String value;
}
