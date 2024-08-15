package com.msb.bpm.approval.appr.client.authority;

import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.authority.AuthorityEndpoint;
import com.msb.bpm.approval.appr.model.request.authority.AuthorityClientRequest;
import com.msb.bpm.approval.appr.model.request.authority.AuthoritySearchRequest;
import com.msb.bpm.approval.appr.model.request.authority.UserAuthorityRequest;
import com.msb.bpm.approval.appr.model.response.authority.AuthorityCheckResponse;
import com.msb.bpm.approval.appr.model.response.authority.AuthorityDataResponse;
import com.msb.bpm.approval.appr.model.response.authority.UserAuthorityReceptionResponse;
import com.msb.bpm.approval.appr.model.response.authority.UserAuthorityResponse;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@AllArgsConstructor
public class AuthorityClient extends AbstractClient {

  private final AuthorityClientConfigProperties configProperties;
  private final RestTemplate restTemplate;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }


  public UserAuthorityResponse searchAuthority(UserAuthorityRequest request, String token) {
    return searchUserAuthority(AuthorityEndpoint.SEARCH_USER_AUTHORITY, request, token);
  }

  public AuthorityDataResponse searchAuthority(AuthoritySearchRequest request, String token) {
    return searchAuthority(AuthorityEndpoint.SEARCH, request, token);
  }

  public AuthorityCheckResponse checkAuthority(AuthorityClientRequest request, String token) {
    return checkAuthority(AuthorityEndpoint.CHECK, request, token);
  }

  public UserAuthorityReceptionResponse getUserByAuthority(String authCode, String token) {
    return getUserByAuthority(AuthorityEndpoint.GROUP_USER, authCode, token);
  }


  private UserAuthorityResponse searchUserAuthority(AuthorityEndpoint authorityEndpoint,
      UserAuthorityRequest request, String token) {
    try {
      String endpoint = this.configProperties.getEndpoint().get(authorityEndpoint.getValue())
          .getUrl();
      log.info("[CALL_API_AUTHORITY] call api {} request : {}", endpoint, request);

      HttpHeaders headers = buildHeader(token);

      String uri = UriComponentsBuilder
          .fromUriString(this.configProperties.getBaseUrl() + endpoint)
          .queryParam("userId", request.getProposalApprovalUser())
          .queryParamIfPresent("authorityClass", Optional.ofNullable(request.getAuthorityClass()))
          .toUriString();

      HttpEntity<UserAuthorityRequest> entity = new HttpEntity<>(
          request, headers);

      UserAuthorityResponse response = exchange(uri, HttpMethod.GET, entity,
          UserAuthorityResponse.class);
      log.info("[CALL_API_AUTHORITY] call api {} response : {}", endpoint, response);
      return response;
    } catch (Exception exception) {
      log.error("method: searchUserAuthority, error ", exception);
      return null;
    }
  }

  private AuthorityDataResponse searchAuthority(AuthorityEndpoint authorityEndpoint,
      AuthoritySearchRequest request, String token) {
    try {
      String endpoint = this.configProperties.getEndpoint().get(authorityEndpoint.getValue())
          .getUrl();
      log.info("[CALL_API_AUTHORITY] call api {} request : {}", endpoint, request);

      HttpHeaders headers = buildHeader(token);

      String uri = UriComponentsBuilder
          .fromUriString(this.configProperties.getBaseUrl() + endpoint)
          .toUriString();

      HttpEntity<AuthoritySearchRequest> entity = new HttpEntity<>(
          request, headers);

      AuthorityDataResponse response = exchange(uri, HttpMethod.POST, entity,
          AuthorityDataResponse.class);
      log.info("[CALL_API_AUTHORITY] call api {} response : {}", endpoint, response);
      return response;
    } catch (Exception exception) {
      log.error("method: searchAuthority, error ", exception);
      return null;
    }
  }

  private AuthorityCheckResponse checkAuthority(AuthorityEndpoint authorityEndpoint,
      AuthorityClientRequest request, String token) {
    try {
      String endpoint = this.configProperties.getEndpoint().get(authorityEndpoint.getValue())
          .getUrl();

      HttpHeaders headers = buildHeader(token);

      String uri = UriComponentsBuilder
          .fromUriString(this.configProperties.getBaseUrl() + endpoint)
          .toUriString();

      request.setRequestId(String.valueOf(UUID.randomUUID()));

      log.info("[CALL_API_AUTHORITY] call api {} request : {}", endpoint, request);

      HttpEntity<AuthorityClientRequest> entity = new HttpEntity<>(
          request, headers);

      AuthorityCheckResponse response = exchange(uri, HttpMethod.POST, entity,
          AuthorityCheckResponse.class);
      log.info("[CALL_API_AUTHORITY] call api {} response : {}", endpoint, response);
      return response;
    } catch (Exception exception) {
      log.error("method: checkAuthority, error ", exception);
      return null;
    }
  }

  private UserAuthorityReceptionResponse getUserByAuthority(AuthorityEndpoint authorityEndpoint, String authCode, String token) {
    try {
      String endpoint = this.configProperties.getEndpoint().get(authorityEndpoint.getValue())
          .getUrl() + "?authCode=" + authCode;

      HttpHeaders headers = buildHeader(token);

      String uri = UriComponentsBuilder
          .fromUriString(this.configProperties.getBaseUrl() + endpoint)
          .toUriString();

      log.info("[CALL_API_AUTHORITY] call api {}", endpoint);

      HttpEntity<AuthorityClientRequest> entity = new HttpEntity<>(null, headers);

      UserAuthorityReceptionResponse response = exchange(uri, HttpMethod.GET, entity, UserAuthorityReceptionResponse.class);
      log.info("[CALL_API_AUTHORITY] call api {} response : {}", endpoint, response);
      return response;
    } catch (Exception exception) {
      log.error("Method: getUserByAuthority, error ", exception);
      return null;
    }
  }

  private HttpHeaders buildHeader(String token) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBearerAuth(token);
    httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
    return httpHeaders;
  }
}