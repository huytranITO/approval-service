package com.msb.bpm.approval.appr.client.cic;

import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.cic.CICEndpoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.cic.SearchCICIntegrationRequest;
import com.msb.bpm.approval.appr.model.request.cic.SearchCICRequest;
import com.msb.bpm.approval.appr.model.request.cic.SearchCodeCICRequest;
import com.msb.bpm.approval.appr.model.response.cic.BaseResponseDto;
import com.msb.bpm.approval.appr.model.response.cic.CICIntegrationResponse;
import com.msb.bpm.approval.appr.model.response.cic.CICResponse;
import com.msb.bpm.approval.appr.model.response.cic.GetPDFDataResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class CICClient extends AbstractClient {

  private static final String GET_CIC_PDF_DATA = "getCICPdfData";
  private static final String CLIENT_QUESTION_KEY = "clientQuestionId";
  private final CICClientConfigProperties configProperties;
  private final RestTemplate restTemplate;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public CICResponse searchCode(SearchCodeCICRequest request, String token) {
    return call(CICEndpoint.SEARCH_CODE, request, token);
  }

  public CICIntegrationResponse search(SearchCICRequest request, String token) {
    return callIntegration(CICEndpoint.SEARCH, request, token);
  }

  public CICIntegrationResponse search(SearchCICIntegrationRequest request, String token) {
    return callIntegration(CICEndpoint.SEARCH_INTEGRATION, request, token);
  }
  private CICResponse call(CICEndpoint cicEndpoint, Object request, String token) {
    try {
      Long startTime = System.currentTimeMillis();
      String endpoint = this.configProperties.getEndpoint().get(cicEndpoint.getValue()).getUrl();
      log.info("[CALL_API_CIC] call api {} request : {}", endpoint, request);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBearerAuth(token);
      httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
      HttpEntity<?> http = new HttpEntity<>(request, httpHeaders);

      String uri = UriComponentsBuilder
          .fromUriString(this.configProperties.getBaseUrl() + endpoint)
          .toUriString();
      CICResponse response = exchange(uri, HttpMethod.POST, http, CICResponse.class);
      log.info("[CALL_API_CIC] call api {} response : {}, time process {}",
          endpoint, response, System.currentTimeMillis() - startTime);
      return response;
    } catch (Exception exception) {
      log.error("method: searchCode, error ", exception);
      if (Objects.equals(DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR,
          ((ApprovalException)exception).getCode())) {
        throw exception;
      }
      throw new ApprovalException(DomainCode.CIC_SERVICE_ERROR, new Object[]{request});
    }
  }
  public GetPDFDataResponse getPDFData(String clientQuestionId) {
    try {
      Long startTime = System.currentTimeMillis();
      String endpoint = this.configProperties.getBaseUrl()
          + this.configProperties.getEndpoint().get(GET_CIC_PDF_DATA).getUrl();
      log.info("[CALL_API_CIC] call api {} clientQuestionId : {}", endpoint, clientQuestionId);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<?> http = new HttpEntity<>(httpHeaders);

      String uri = UriComponentsBuilder.fromUriString(endpoint)
          .queryParam(CLIENT_QUESTION_KEY, clientQuestionId)
          .toUriString();

      BaseResponseDto<GetPDFDataResponse> response
          = exchange(uri, HttpMethod.GET, http,
          new ParameterizedTypeReference<BaseResponseDto<GetPDFDataResponse>>() {});

      log.info("[CALL_API_CIC] call api {} response : {}, time process {}",
          endpoint, response, System.currentTimeMillis() - startTime);
      return response.getValue();
    } catch (Exception exception) {
      log.error("method: getPDFData, error ", exception);
      return null;
    }
  }

  private CICIntegrationResponse callIntegration(CICEndpoint cicEndpoint, Object request, String token) {
    try {
      Long startTime = System.currentTimeMillis();
      String endpoint = this.configProperties.getEndpoint().get(cicEndpoint.getValue()).getUrl();
      log.info("[CALL_API_INTEGRATION_CIC] call api {} request : {}", endpoint, request);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBearerAuth(token);
      httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
      HttpEntity<?> http = new HttpEntity<>(request, httpHeaders);
      log.info("[CALL_API_INTEGRATION_CIC] with token:  {}", token);
      log.info("[CALL_API_INTEGRATION_CIC] with header:  {}", httpHeaders);
      String uri = UriComponentsBuilder
              .fromUriString(this.configProperties.getBaseUrlIntegration() + endpoint)
              .toUriString();
      CICIntegrationResponse response = exchange(uri, HttpMethod.POST, http, CICIntegrationResponse.class);
      log.info("[CALL_API_INTEGRATION_CIC] call api {} response : {}, time process {}",
              endpoint, response, System.currentTimeMillis() - startTime);
      return response;
    } catch (Exception exception) {
      log.error("method: searchCodeIntegration, error ", exception);
      if (Objects.equals(DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR,
              ((ApprovalException)exception).getCode())) {
        throw exception;
      }
      throw new ApprovalException(DomainCode.CIC_SERVICE_ERROR, new Object[]{request});
    }
  }

  public GetPDFDataResponse getPDFData(String clientQuestionId, String token) {
    try {
      Long startTime = System.currentTimeMillis();
      String endpoint = this.configProperties.getBaseUrl()
              + this.configProperties.getEndpoint().get(GET_CIC_PDF_DATA).getUrl();
      log.info("RB [CALL_API_CIC] call api {} clientQuestionId : {}", endpoint, clientQuestionId);
      log.info("RB [CALL_API_CIC] call api with token : {}", token);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBearerAuth(token);
      HttpEntity<?> http = new HttpEntity<>(httpHeaders);

      String uri = UriComponentsBuilder.fromUriString(endpoint)
              .queryParam(CLIENT_QUESTION_KEY, clientQuestionId)
              .toUriString();

      BaseResponseDto<GetPDFDataResponse> response
              = exchange(uri, HttpMethod.GET, http,
              new ParameterizedTypeReference<BaseResponseDto<GetPDFDataResponse>>() {});

      log.info("RB [CALL_API_CIC] call api {} response : {}, time process {}",
              endpoint, response, System.currentTimeMillis() - startTime);
      return response.getValue();
    } catch (Exception exception) {
      log.error("RB method: getPDFData, error ", exception);
      return null;
    }
  }
}
