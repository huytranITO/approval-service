package com.msb.bpm.approval.appr.client.css;

import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.css.CSSEndpoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.css.GetScoreRBLegacyRequest;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.GetScoreRBResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;

import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@AllArgsConstructor
public class CSSClient extends AbstractClient {

  private final RestTemplate restTemplate;
  private final CSSClientConfigProperties configProperties;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public GetScoreRBResponse getScoreRB(GetScoreRBLegacyRequest request) {
    try {
      log.info("[CALL_API_CSS] call api request : {}", request);
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBearerAuth(HeaderUtil.getToken());
      httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      httpHeaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<GetScoreRBLegacyRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

      String uri = UriComponentsBuilder.fromUriString(this.configProperties.getBaseUrl()
           + this.configProperties.getEndpoint().get(CSSEndpoint.GET_SCORE_RB.getValue()).getUrl())
          .toUriString();
      GetScoreRBResponse response = exchange(uri, HttpMethod.POST, httpEntity,
          GetScoreRBResponse.class);
      log.info("[CALL_API_CSS] response : {}", response);
      return response;
    } catch (Exception exception) {
      log.error("method: getScoreRB, error ", exception);
      throw new ApprovalException(DomainCode.CSS_SYSTEM_ERROR, new Object[]{request.getInfo()
          .getLegalDocNo()});
    }
  }
}
