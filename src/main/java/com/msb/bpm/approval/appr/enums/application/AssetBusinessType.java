package com.msb.bpm.approval.appr.enums.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetBusinessType {

  BPM("BPM"),
  LDP("LANDING_PAGE");

  private final String value;
}
