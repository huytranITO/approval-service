package com.msb.bpm.approval.appr.model.response.usermanager;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserManagerPermission {

  private String code;
  private String message;

  private DataResponse data;

  @Data
  public static class DataResponse {

    private boolean hasPermissions;

  }
}
