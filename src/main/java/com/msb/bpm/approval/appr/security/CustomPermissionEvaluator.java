package com.msb.bpm.approval.appr.security;

import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

  private final UserManagerClient userManagerClient;

  @Override
  public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {

    if (Objects.isNull(auth) || Objects.isNull(permission)) {
      return false;
    }

    return userManagerClient.getPermission(permission.toString());
  }

  @Override
  public boolean hasPermission(Authentication auth, Serializable targetId, String targetType,
      Object permission) {

    if (Objects.isNull(auth) || Objects.isNull(targetType)) {
      return false;
    }

    return userManagerClient.getPermission(permission.toString());
  }
}

