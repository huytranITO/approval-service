package com.msb.bpm.approval.appr.client.t24;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.t24.T24Endpoint;
import com.msb.bpm.approval.appr.model.request.t24.T24Request;
import com.msb.bpm.approval.appr.model.response.t24.T24AccountResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@AllArgsConstructor
public class T24Client extends AbstractClient {
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final T24ConfigProperties properties;
  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }
  public T24AccountResponse callT24(T24Request request) {
      String endpoint = this.properties
          .getEndpoint()
          .get(T24Endpoint.GET_T24_ACCOUNT.getValue())
          .getUrl();

      log.info("[GET_T24_ACCOUNT_INFO] call endpoint {} request : {}",
          endpoint, JsonUtil.convertObject2String(request, objectMapper));

      HttpEntity<?> http = new HttpEntity<>(request, buildCommonHeaders(HeaderUtil.getToken()));

      String uri = UriComponentsBuilder
          .fromUriString(this.properties.getBaseUrl() + endpoint)
          .toUriString();

    T24AccountResponse esbResponse = exchange(uri, HttpMethod.POST, http, T24AccountResponse.class);

      log.info("[GET_T24_ACCOUNT_INFO] call endpoint {}, request : {}, response : {}", endpoint,
          JsonUtil.convertObject2String(request, objectMapper),
          JsonUtil.convertObject2String(esbResponse, objectMapper));
      return esbResponse;
  }
}
