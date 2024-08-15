package com.msb.bpm.approval.appr.client.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.config.OpriskPersonConfig;
import com.msb.bpm.approval.appr.model.request.oprisk.CheckBlackListPLegalRequest;
import com.msb.bpm.approval.appr.model.response.oprisk.SyncOpRiskAssetResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpRiskLegacyClient extends AbstractClient {
  private final RestTemplate restTemplate;
  private final OpriskPersonConfig opriskPersonConfig;
  private final ObjectMapper objectMapper;
  @Override
  protected RestTemplate getRestTemplate() {
    return this.restTemplate;
  }

  public SyncOpRiskAssetResponse syncOpriskLegal(CheckBlackListPLegalRequest request) {
    log.info("[CALL_API_SYNC_OPRISK_LEGAL] with request : {}", JsonUtil.convertObject2String(request, objectMapper));

    String uri = UriComponentsBuilder.fromUriString(opriskPersonConfig.getUrl())
        .toUriString();

    HttpHeaders headers = buildCommonHeaders(HeaderUtil.getToken());
    HttpEntity<CheckBlackListPLegalRequest> entity = new HttpEntity<>(request, headers);

    SyncOpRiskAssetResponse response = super.exchange(uri, HttpMethod.POST, entity, SyncOpRiskAssetResponse.class);
      log.info("[CALL_API_SYNC_OPRISK_LEGAL] with request: {}, response: {}",
          JsonUtil.convertObject2String(request, objectMapper),
          JsonUtil.convertObject2String(response, objectMapper));

    return response;
  }
}
