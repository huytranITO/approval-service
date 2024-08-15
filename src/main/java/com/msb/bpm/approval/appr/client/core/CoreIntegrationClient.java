package com.msb.bpm.approval.appr.client.core;


import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.config.core.properties.CoreIntegrationProperties;
import com.msb.bpm.approval.appr.client.core.request.CommonRequest;
import com.msb.bpm.approval.appr.client.core.request.CoreCustomerRequest;
import com.msb.bpm.approval.appr.client.core.response.CoreCustomerCoreIntegrationResponse;
import com.msb.bpm.approval.appr.client.core.response.CoreCustomerInfo;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class CoreIntegrationClient extends AbstractClient {

  private static final String RESOURCE = "CUSTOMER_ADDITIONAL_SERVICE";
  private static final String GET_CUSTOMER_BY_CIF_AND_ID_NUM = "getCustomerByCifAndIdNum";

  private final RestTemplate restTemplate;
  private final CoreIntegrationProperties coreIntegrationProperties;

  public CoreIntegrationClient(
      @Qualifier("coreIntegrationRestTemplate") RestTemplate restTemplate,
      CoreIntegrationProperties coreIntegrationProperties) {
    this.restTemplate = restTemplate;
    this.coreIntegrationProperties = coreIntegrationProperties;
  }

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public CoreCustomerInfo getCustomerInfo(String cifNo, String identityNumber, String token) {
    try {
      CommonRequest commonRequest = CommonRequest.builder()
          .requestId(UUID.randomUUID().toString())
          .source(RESOURCE)
          .build();
      CoreCustomerRequest customerRequest = CoreCustomerRequest.builder()
          .cifNumber(cifNo)
          .idNumber(identityNumber)
          .commonInfo(commonRequest)
          .build();

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, token);
      HttpEntity<CoreCustomerRequest> httpEntity = new HttpEntity<>(customerRequest, headers);

      String uri = UriComponentsBuilder.fromUriString(this.coreIntegrationProperties.getBaseUrl()
              + this.coreIntegrationProperties.getEndpoint().get(GET_CUSTOMER_BY_CIF_AND_ID_NUM))
          .toUriString();

      return exchange(uri, HttpMethod.POST, httpEntity, CoreCustomerCoreIntegrationResponse.class)
          .getData().getCustomer();

    } catch (Exception exception) {
      log.error("method: getCustomerInfo, error ", exception);
      return null;
    }
  }
}
