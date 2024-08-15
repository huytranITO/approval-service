package com.msb.bpm.approval.appr.client;

import static com.msb.bpm.approval.appr.exception.DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR;
import static com.msb.bpm.approval.appr.exception.DomainCode.EXTERNAL_SERVICE_SERVER_ERROR;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public abstract class AbstractClient {

  protected abstract RestTemplate getRestTemplate();

  protected <T> T exchangeCustomHandler(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
      Class<T> responseType, Object... uriVariables) {
    return getRestTemplate().exchange(url, method, requestEntity, responseType, uriVariables)
        .getBody();
  }

  protected <T> T exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
      Class<T> responseType, Object... uriVariables) {
    try {
      ResponseEntity<T> respEntity = getRestTemplate()
          .exchange(url, method, requestEntity, responseType, uriVariables);
      return respEntity.getBody();
    } catch (HttpClientErrorException e) {
      log.error(
          "method: exchange, HttpClientErrorException, url: {}, httpStatus: {}, body: {}, error: {}"
          , url, e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
      throw new ApprovalException(
          EXTERNAL_SERVICE_CLIENT_ERROR, new Object[]{e.getMessage() ,e.getResponseBodyAsString()});
    } catch (HttpServerErrorException e) {
      log.error("method: exchange, HttpServerErrorException, url: {}, httpStatus: {}, body: {}, error: {}", url, e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
      throw new ApprovalException(EXTERNAL_SERVICE_SERVER_ERROR, new Object[]{e.getResponseBodyAsString(), e.getStatusCode().value(), e.getMessage()});
    } catch (RestClientException e) {
      log.error("method: exchange, RestClientException, url: {}, error: {}", url, e.getMessage());
      throw new ApprovalException(EXTERNAL_SERVICE_SERVER_ERROR, new Object[]{e.getMessage()});
    }
  }

  protected <T> T exchangeForTimeout(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) {
    try {
      ResponseEntity<T> respEntity = getRestTemplate().exchange(url, method, requestEntity, responseType, uriVariables);
      return respEntity.getBody();
    } catch (HttpClientErrorException e) {
      log.error("method: exchange, HttpClientErrorException, url: {}, httpStatus: {}, body: {}, error: {}"
          , url, e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
      throw new ApprovalException(EXTERNAL_SERVICE_CLIENT_ERROR, new Object[]{e.getResponseBodyAsString(), e.getStatusCode().value()});
    } catch (HttpServerErrorException e) {
      log.error("method: exchange, HttpServerErrorException, url: {}, httpStatus: {}, body: {}, error: {}", url, e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
      throw new ApprovalException(EXTERNAL_SERVICE_SERVER_ERROR, new Object[]{e.getResponseBodyAsString(), e.getStatusCode().value(), e.getMessage()});
    } catch (RestClientException e) {
      log.error("method: exchange, RestClientException, url: {}, error: {}", url, e.getMessage());
      throw new ApprovalException(EXTERNAL_SERVICE_SERVER_ERROR, new Object[]{e.getMessage()});
    }
  }


  protected <T> T exchange(String url, HttpMethod method,
      @Nullable HttpEntity<?> requestEntity,
      ParameterizedTypeReference<T> responseType,
      Object... uriVariables) {
    try {
      ResponseEntity<T> respEntity = getRestTemplate()
          .exchange(url, method, requestEntity, responseType, uriVariables);
      return respEntity.getBody();
    } catch (HttpClientErrorException e) {
      log.error("method: exchange2, HttpClientErrorException, url: {}, httpStatus: {}, body: {}, error: {}"
          , url, e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
      throw new ApprovalException(
          EXTERNAL_SERVICE_CLIENT_ERROR);
    } catch (HttpServerErrorException e) {
      log.error("method: exchange2, HttpServerErrorException, url: {}, httpStatus: {}, body: {}, error: {}"
          , url, e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
      throw new ApprovalException(
          EXTERNAL_SERVICE_SERVER_ERROR);
    } catch (RestClientException e) {
      log.error("method: exchange2, RestClientException, url: {}, error: {}", url, e.getMessage());
      throw new ApprovalException(EXTERNAL_SERVICE_SERVER_ERROR);
    }
  }


  protected HttpHeaders buildCommonHeaders(String token){

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(token);
    return headers;
  }
}