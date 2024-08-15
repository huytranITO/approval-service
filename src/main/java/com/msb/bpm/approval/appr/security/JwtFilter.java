package com.msb.bpm.approval.appr.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a
 * valid user is found.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  private final ApiRespFactory apiRespFactory;
  private final TokenProvider tokenProvider;
  private final ObjectMapper mapper;

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    String jwt = resolveToken(httpServletRequest);
    if (StringUtils.hasText(jwt)) {
      try {
        this.tokenProvider.parseToken(jwt);
        Authentication authentication = this.tokenProvider.getAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (ExpiredJwtException ex) {
        log.error("Method: doFilter, jwt expired : ", ex);
        ResponseEntity<ApiResponse> errorResponse = apiRespFactory.failWithDomainException(
            new ApprovalException(DomainCode.TOKEN_EXPIRED));
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.setStatus(HttpStatus.UNAUTHORIZED.value());
        this.mapper.writeValue(servletResponse.getOutputStream(), errorResponse);
        return;
      } catch (Exception ex) {
        log.error("Method: doFilter, exception : ", ex);
        ResponseEntity<ApiResponse> errorResponse = apiRespFactory.failWithDomainException(
            new ApprovalException(DomainCode.INTERNAL_SERVICE_ERROR));
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        this.mapper.writeValue(servletResponse.getOutputStream(), errorResponse);
        return;
      }
    }
    filterChain.doFilter(servletRequest, servletResponse);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
