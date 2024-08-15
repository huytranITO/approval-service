package com.msb.bpm.approval.appr.client.aml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.aml.AMLEndpoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.response.legacy.impl.aml.GetAMLInfoResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.util.Objects;
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
public class AMLClient extends AbstractClient {

  public static final String ID_PASSPORT = "idPassport";

  private final RestTemplate restTemplate;
  private final AMLClientConfigProperties configProperties;
  private final ObjectMapper objectMapper;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public GetAMLInfoResponse getAmlInfo(String idPassport) {
    try {
      String endpoint = this.configProperties
          .getEndpoint()
          .get(AMLEndpoint.GET_AML_INFO.getValue())
          .getUrl();

      log.info("[CALL_API_AML] call api {} request : {}", endpoint, idPassport);

      String token = HeaderUtil.getToken();
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBearerAuth(token);
      httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
      HttpEntity<?> http = new HttpEntity<>(httpHeaders);

      String uri = UriComponentsBuilder
          .fromUriString(this.configProperties.getBaseUrl() + endpoint)
          .queryParam(ID_PASSPORT, idPassport)
          .toUriString();
      GetAMLInfoResponse response = exchange(uri, HttpMethod.GET, http, GetAMLInfoResponse.class);
      log.info("[CALL_API_AML] call api {}, request : {}, response : {}", endpoint,
          JsonUtil.convertObject2String(idPassport, objectMapper),
          JsonUtil.convertObject2String(response, objectMapper));
      return response;
    } catch (Exception exception) {
      log.error("method: getAmlInfo with request={}, error: ", idPassport, exception);
      if (Objects.equals(DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR,
          ((ApprovalException)exception).getCode())) {
        throw exception;
      }
      throw new ApprovalException(DomainCode.AML_SERVICE_ERROR, new Object[]{idPassport});
    }
  }
}
