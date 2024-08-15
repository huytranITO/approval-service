package com.msb.bpm.approval.appr.client.oprisk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.CheckBlackListPLegalRequest;
import com.msb.bpm.approval.appr.model.request.CheckBlackListPPersonRequest;
import com.msb.bpm.approval.appr.model.response.legacy.impl.oprisklegal.SyncOpriskLegalResponse;
import com.msb.bpm.approval.appr.model.response.legacy.impl.opriskperson.SyncOpriskPersonResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpriskClient extends AbstractClient {

  private final RestTemplate restTemplate;
  @Value("${approval-service.client.oprisk.url}")
  private String opRiskUrl;
  private final ObjectMapper objectMapper;
  @Retryable(value = ApprovalException.class, maxAttemptsExpression = "${approval-service.client.oprisk.retry.max-attempts}",
      backoff = @Backoff(delayExpression = "${approval-service.client.oprisk.retry.max-delay}"))
  public SyncOpriskPersonResponse syncOpriskPerson(CheckBlackListPPersonRequest checkBlackListPPersonRequest) {
    log.info("method: syncOprisk Start with request={}",
        JsonUtil.convertObject2String(checkBlackListPPersonRequest, objectMapper));
    SyncOpriskPersonResponse response;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<CheckBlackListPPersonRequest> entity = new HttpEntity<>(
          checkBlackListPPersonRequest, headers);
      response = super.exchange(opRiskUrl, HttpMethod.POST, entity, SyncOpriskPersonResponse.class);
      log.info("method: syncOprisk End with request={}, response={}",
          JsonUtil.convertObject2String(checkBlackListPPersonRequest, objectMapper),
          JsonUtil.convertObject2String(response, objectMapper));
    } catch (Exception e) {
        log.error("syncOpriskPerson with request={}, error: ",
            JsonUtil.convertObject2String(checkBlackListPPersonRequest, objectMapper), e);
        throw new ApprovalException(DomainCode.OPRISK_SERVICE_ERROR, new Object[]{
            checkBlackListPPersonRequest.getCheckBlackListP().getIdentityCard()});
    }
    return response;
  }

  @Retryable(value = ApprovalException.class, maxAttemptsExpression = "${approval-service.client.oprisk.retry." +
    "max-attempts}", backoff = @Backoff(delayExpression = "${approval-service.client.oprisk.retry.max-delay}"))
  public SyncOpriskLegalResponse syncOpriskLegal(CheckBlackListPLegalRequest checkBlackListPLegalRequest) {
    log.info("method: syncOprisk Start with checkBlackListPLegalRequest={}",
        JsonUtil.convertObject2String(checkBlackListPLegalRequest, objectMapper));
    SyncOpriskLegalResponse response;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<CheckBlackListPLegalRequest> entity = new HttpEntity<>(checkBlackListPLegalRequest,
          headers);
      response = super.exchange(opRiskUrl, HttpMethod.POST, entity, SyncOpriskLegalResponse.class);
      log.info("method: syncOpriskLegal End with request={}, response={}",
          JsonUtil.convertObject2String(checkBlackListPLegalRequest, objectMapper),
          JsonUtil.convertObject2String(response, objectMapper));
    } catch (Exception e) {
      log.error("syncOpriskLegal with request={}, error: ",
          JsonUtil.convertObject2String(checkBlackListPLegalRequest, objectMapper), e);
      throw new ApprovalException(DomainCode.OPRISK_SERVICE_ERROR, new Object[]{
          checkBlackListPLegalRequest.getCheckBlackListO().getBusinessRegistrationNumber()});
    }
    return response;
  }

  @Override
  protected RestTemplate getRestTemplate() {
    return this.restTemplate;
  }
}
