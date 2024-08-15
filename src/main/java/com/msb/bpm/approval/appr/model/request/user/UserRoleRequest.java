package com.msb.bpm.approval.appr.model.request.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleRequest {
  private Set<String> roles;
  private String organizationCode;
  private String status;
}
