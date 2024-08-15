package com.msb.bpm.approval.appr.enums.authority;

import lombok.Getter;

@Getter
public enum AuthorityEndpoint {
  SEARCH("search"),
  SEARCH_USER_AUTHORITY("user"),
  CHECK("check"),
  GROUP_USER("group-user");

  private final String value;

  AuthorityEndpoint(String value) {
    this.value = value;
  }
}