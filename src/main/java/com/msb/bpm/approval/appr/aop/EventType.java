package com.msb.bpm.approval.appr.aop;

import lombok.Getter;

@Getter
public enum EventType {
  GENERAL_INFO("GENERAL_INFO"), CMS_INFO("CMS_INFO"), CMS_V2_INFO("CMS_V2_INFO");

  private final String value;

  EventType(String value) {
    this.value = value;
  }
}
