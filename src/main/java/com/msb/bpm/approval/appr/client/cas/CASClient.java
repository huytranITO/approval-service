package com.msb.bpm.approval.appr.client.cas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.cas.CASEndpoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.cas.PostCASRequest;
import com.msb.bpm.approval.appr.model.response.cas.CASResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Slf4j
@Component
@AllArgsConstructor
public class CASClient extends AbstractClient {

  private final RestTemplate restTemplate;
  private final CASClientConfigProperties configProperties;
  private final ObjectMapper objectMapper;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public CASResponse getCASScore(PostCASRequest request) {
    try {
      log.info("[CALL_API_CAS] call api request : {}", request);
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBearerAuth(HeaderUtil.getToken());
      httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      httpHeaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<PostCASRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

      String uri = UriComponentsBuilder.fromUriString(this.configProperties.getBaseUrl()
           + this.configProperties.getEndpoint().get(CASEndpoint.GET_CAS_SCORE.getValue()).getUrl())
          .toUriString();
      CASResponse response = exchange(uri, HttpMethod.POST, httpEntity, CASResponse.class);
      log.info("[CALL_API_CAS] response : {}", JsonUtil.convertObject2String(response, objectMapper));
      return response;
    } catch (Exception exception) {
      log.error("method: getCASScore, error ", exception);
      throw new ApprovalException(DomainCode.CAS_SYSTEM_ERROR, new Object[]{request});
    }
  }
}
