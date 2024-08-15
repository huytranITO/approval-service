package com.msb.bpm.approval.appr.security;

import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.SSLHostConfigCertificate.Type;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {

  private final ApplicationConfig applicationConfig;

  @SuppressWarnings("unchecked")
  public Authentication getAuthentication(String token) {

    Claims claims = parseToken(token).getBody();

    String username = claims.getSubject();

    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    ((List<String>) claims.get("roles")).forEach(
        r -> authorities.add(new SimpleGrantedAuthority(r)));

    User principal = new User(username, "", authorities);

    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
  }

  public Jws<Claims> parseToken(String token) {
    if (Type.RSA.name().equals(applicationConfig.getJwt().getAlgorithm())) {
      return Jwts.parserBuilder().setSigningKey(
              SecurityContextUtil.initRSAKey(applicationConfig.getJwt().getSecretKey())).build()
          .parseClaimsJws(token);
    }
    return Jwts.parserBuilder().setSigningKey(
            Keys.hmacShaKeyFor(applicationConfig.getJwt().getSecretKey().getBytes())).build()
        .parseClaimsJws(token);
  }
}
