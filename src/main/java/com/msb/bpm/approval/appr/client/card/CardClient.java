package com.msb.bpm.approval.appr.client.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.config.card.properties.CardClientProperties;
import com.msb.bpm.approval.appr.config.card.request.CheckCardCreatedRequest;
import com.msb.bpm.approval.appr.config.card.request.CreateCardRequest;
import com.msb.bpm.approval.appr.config.card.request.CreateClientRequest;
import com.msb.bpm.approval.appr.config.card.request.CreateSubCardRequest;
import com.msb.bpm.approval.appr.config.card.response.CheckCardCreatedResponse;
import com.msb.bpm.approval.appr.config.card.response.CheckClientResponse;
import com.msb.bpm.approval.appr.config.card.response.CreateCardResponse;
import com.msb.bpm.approval.appr.config.card.response.CreateClientResponse;
import com.msb.bpm.approval.appr.config.card.response.CreateSubCardResponse;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.util.Collections;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/*
* @author: BaoNV2
* @since: 29/9/2023 10:09 AM
*
* */

@Slf4j
@Component
public class CardClient extends AbstractClient {
  private final String CREATE_CLIENT = "createClient";
  private final String CREATE_CARD = "createCard";
  private final String CREATE_SUB_CARD = "createSubCard";
  private final String CHECK_CREATED = "checkCreated";
  private final String CHECK_CLIENT = "checkClient";
  private final RestTemplate restTemplate;
  private final CardClientProperties cardClientProperties;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public CardClient(@Qualifier("cardRestTemplate") RestTemplate restTemplate,
      CardClientProperties cardClientProperties) {
    {
      this.restTemplate = restTemplate;
      this.cardClientProperties = cardClientProperties;
    }
  }

  public CreateClientResponse createClient(CreateClientRequest request) {
    log.info("createClient START with request={}", request);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<CreateClientRequest> entity = new HttpEntity<>(request, headers);
      String uri = UriComponentsBuilder.fromUriString(this.cardClientProperties.getBaseUrl() + this.cardClientProperties.getEndpoint().get(CREATE_CLIENT)).toUriString();
      String object = super.exchangeForTimeout(uri, HttpMethod.POST, entity, String.class);

      CreateClientResponse response = JsonUtil.convertString2Object(object, CreateClientResponse.class, new ObjectMapper());
      log.info("createClient END with request={}, response={}", request, response);
      return response;
    } catch (Exception e) {
      log.error("createClient with request={} -> Error: {}", request, e.getMessage());
      throw e;
    }
  }

  public CreateCardResponse createCard(CreateCardRequest request) throws ApprovalException {
    log.info("createCard START with request={}", request);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<CreateCardRequest> entity = new HttpEntity<>(request, headers);
      String uri = UriComponentsBuilder.fromUriString(this.cardClientProperties.getBaseUrl()
          + this.cardClientProperties.getEndpoint().get(CREATE_CARD)).toUriString();

      String object = super.exchangeForTimeout(uri, HttpMethod.POST, entity, String.class);

      CreateCardResponse response = JsonUtil.convertString2Object(object, CreateCardResponse.class,
          new ObjectMapper());
      log.info("createCard END with request={}, response={}", request, response);
      return response;
    } catch (Exception e) {
      log.error("createCard with request={} -> Error: {}", request, e.getMessage());
      throw e;
    }
  }

  public CreateSubCardResponse createSubCard(CreateSubCardRequest request) {
    log.info("createSubCard START with request={}", request);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.add("X-Request-Id", String.valueOf(UUID.randomUUID()));
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<CreateSubCardRequest> entity = new HttpEntity<>(request, headers);
      String uri = UriComponentsBuilder.fromUriString(this.cardClientProperties.getBaseUrl()
              + this.cardClientProperties.getEndpoint().get(CREATE_SUB_CARD))
          .toUriString();
      String object = super.exchangeForTimeout(uri, HttpMethod.POST, entity, String.class);

      CreateSubCardResponse response = JsonUtil.convertString2Object(object, CreateSubCardResponse.class,
          new ObjectMapper());
      log.info("createSubCard END with request={}, response={}", request, response);
      return response;
    } catch (Exception e) {
      log.error("createSubCard with request={} -> Error: {}", request, e.getMessage());
      throw e;
    }
  }

  public CheckCardCreatedResponse checkCreated(CheckCardCreatedRequest request) {
    log.info("checkCreated START with request={}", request);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.add("X-Request-Id", String.valueOf(UUID.randomUUID()));
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<CheckCardCreatedRequest> entity = new HttpEntity<>(request, headers);
      String uri = UriComponentsBuilder.fromUriString(this.cardClientProperties.getBaseUrl() + this.cardClientProperties.getEndpoint().get(CHECK_CREATED)).toUriString();
      String object = super.exchangeForTimeout(uri, HttpMethod.POST, entity, String.class);

      CheckCardCreatedResponse response = JsonUtil.convertString2Object(object, CheckCardCreatedResponse.class, new ObjectMapper());
      log.info("checkCreated END with request={}, response={}", request, response);
      return response;
    } catch (Exception e) {
      log.error("checkCreated with request={} -> Error: {}", request, e.getMessage());
      throw e;
    }
  }

  public CheckClientResponse checkClient(String cifNo) {
    log.info("checkClient START with cifNo={}", cifNo);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<?> entity = new HttpEntity<>(headers);
      String uri = UriComponentsBuilder.fromUriString(this.cardClientProperties.getBaseUrl() + this.cardClientProperties.getEndpoint().get(CHECK_CLIENT))
          .queryParam("cif-number", cifNo)
          .toUriString();
      CheckClientResponse response = super.exchange(uri, HttpMethod.GET, entity, CheckClientResponse.class);
      log.info("checkClient END with request={}, response={}", response);
      return response;
    } catch (Exception e) {
      log.error("checkClient with request={} -> Error: {}", e.getMessage());
      throw e;
    }
  }
}
