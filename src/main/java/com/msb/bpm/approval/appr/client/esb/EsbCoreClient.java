package com.msb.bpm.approval.appr.client.esb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.esb.EsbEndpoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.esb.EsbRequest;
import com.msb.bpm.approval.appr.model.response.esb.EsbResponse;
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
public class EsbCoreClient extends AbstractClient {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final EsbCoreConfigProperties properties;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public EsbResponse callGetEsbAccountInfo(EsbRequest request) {
    try {
      String endpoint = this.properties
          .getEndpoint()
          .get(EsbEndpoint.GET_ESB_ACCOUNT.getValue())
          .getUrl();

      log.info("[CALL_GET_ESB_ACCOUNT_INFO] call api {} request : {}",
          endpoint, JsonUtil.convertObject2String(request, objectMapper));

      String token = HeaderUtil.getToken();
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBearerAuth(token);
      httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
      HttpEntity<?> http = new HttpEntity<>(request, httpHeaders);

      String uri = UriComponentsBuilder
          .fromUriString(this.properties.getBaseUrl() + endpoint)
          .toUriString();

      EsbResponse esbResponse = exchange(uri, HttpMethod.POST, http, EsbResponse.class);

      log.info("[CALL_GET_ESB_ACCOUNT_INFO] call api {}, request : {}, response : {}", endpoint,
          JsonUtil.convertObject2String(request, objectMapper),
          JsonUtil.convertObject2String(esbResponse, objectMapper));
      return esbResponse;
    } catch (Exception e) {
      log.error("method: callGetEsbAccountInfo with request = {}, error: ",
          JsonUtil.convertObject2String(request, objectMapper), e);
      throw new ApprovalException(DomainCode.ESB_CORE_CLIENT_ERROR);
    }

  }

}
