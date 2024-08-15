package com.msb.bpm.approval.appr.client.rule;


import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.rule.RuleEndpoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.BodyRequest;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/*
* @author: BaoNV2
* @since: 15/5/2023 1:18 PM
* @description:  
* @update:
*
* */
@Slf4j
@Component
@AllArgsConstructor
public class RuleClient extends AbstractClient  {
  private RuleClientConfigProperties ruleProperties;

  private final RestTemplate restTemplate;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public Object searchByCode(BodyRequest<Object> request, String token) {
    return call(RuleEndpoint.SEARCH_CODE, request, token);
  }

  private Object call(RuleEndpoint ruleEndpoint, Object request, String token) {
    try {
      String endpoint = this.ruleProperties.getEndpoint().get(ruleEndpoint.getValue()).getUrl();
      log.info("[CALL_API_RULE] call api {} request : {}", this.ruleProperties.getBaseUrl() + endpoint, request);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBearerAuth(token);
      httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
      HttpEntity<?> http = new HttpEntity<>(request, httpHeaders);

      String uri = UriComponentsBuilder
          .fromUriString(this.ruleProperties.getBaseUrl() + endpoint)
          .toUriString();
      Object response = exchange(uri, HttpMethod.POST, http, Object.class);
      log.info("[CALL_API_RULE] call api {} response : {}", endpoint, response);
      return response;
    } catch (Exception exception) {
      log.error("method: searchCode, error ", exception.getMessage());
      if (Objects.equals(DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR,
          ((ApprovalException)exception).getCode())) {
        throw exception;
      }
      throw new ApprovalException(DomainCode.RULE_MANAGEMENT_ERROR, new Object[]{request});
    }
  }

}
