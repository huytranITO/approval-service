package com.msb.bpm.approval.appr.util;

import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityContextUtil {

  public static Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public static String getCurrentUser() {
    if (getAuthentication() == null) {
      return "unknown";
    }
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  public static String getUsername() {
    if (getAuthentication() == null) {
      throw new ApprovalException(DomainCode.FORBIDDEN);
    }
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  public static String getAuthorizationToken() {
    return (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
  }

  public static Set<String> getAuthorities() {
    Authentication authentication = getAuthentication();
    if (authentication == null) {
      throw new ApprovalException(DomainCode.FORBIDDEN);
    }
    return new HashSet<>(authentication.getAuthorities()).stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
  }

  public static RSAPublicKey initRSAKey(String publicKey) {
    try {
      byte[] decode = Base64.getDecoder().decode(publicKey);
      X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(decode);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return (RSAPublicKey) keyFactory.generatePublic(keySpecX509);
    } catch (Exception e) {
      return null;
    }
  }
}
