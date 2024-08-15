package com.msb.bpm.approval.appr.util;

import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class HeaderUtil {

  public static String getToken() {
    String token = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      token = authentication.getCredentials().toString();
    }
    return token;
  }

  public static String basicAuthen(String username, String password) {
    String auth = String.join(":", username, password);
    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
    return "Basic " + new String(encodedAuth);
  }


}
