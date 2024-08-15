package com.msb.bpm.approval.appr.client.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.legacy.LegacyEndpoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.legacy.CreditRatingCssRbRequest;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.GetScoreRbCssResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
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
public class LegacyClient extends AbstractClient {
  private final RestTemplate restTemplate;
  private final LegacyClientConfigProperties properties;
  private final ObjectMapper objectMapper;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public GetScoreRbCssResponse getCreditRating(CreditRatingCssRbRequest request) {
    try {
      String endpoint = this.properties
          .getEndpoint()
          .get(LegacyEndpoint.GET_CREDIT_RATING_RB.getValue())
          .getUrl();

      log.info("[CALL_API_LEGACY_SERVICE] call api {} and request : {}",
          endpoint, JsonUtil.convertObject2String(request, objectMapper));

      String token = HeaderUtil.getToken();
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBearerAuth(token);
      httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
      HttpEntity<?> http = new HttpEntity<>(request, httpHeaders);

      String uri = UriComponentsBuilder
          .fromUriString(this.properties.getBaseUrl() + endpoint)
          .toUriString();

      GetScoreRbCssResponse getScoreRbCssResponse = exchange(uri, HttpMethod.POST, http, GetScoreRbCssResponse.class);

      log.info("[CALL_API_LEGACY_SERVICE] call api {} with request : {}, response : {}", endpoint,
          JsonUtil.convertObject2String(request, objectMapper),
          JsonUtil.convertObject2String(getScoreRbCssResponse, objectMapper));

      return getScoreRbCssResponse;
    } catch (Exception exception) {
      log.error("method: getCreditRating with request {} error: ",
          JsonUtil.convertObject2String(request, objectMapper), exception);
      throw new ApprovalException(DomainCode.CSS_SYSTEM_ERROR, new Object[]{request
          .getLegalDocNo()});
    }
  }
}
