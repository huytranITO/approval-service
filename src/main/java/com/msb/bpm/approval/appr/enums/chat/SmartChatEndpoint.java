package com.msb.bpm.approval.appr.enums.chat;

import lombok.Getter;

@Getter
public enum SmartChatEndpoint {
  CREATE_GROUP("create-group"),
  CLOSE_GROUP("close-group"),

  ADD_USER_TO_ROOM("add-user-to-room")
  ;

  private final String value;

  SmartChatEndpoint(String value) {
    this.value = value;
  }
}
