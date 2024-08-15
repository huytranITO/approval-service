package com.msb.bpm.approval.appr.client.usermanager.v2;

import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.client.usermanager.v2.config.properties.UserClientProperties;
import com.msb.bpm.approval.appr.client.usermanager.v2.config.response.GetUserResponse;
import com.msb.bpm.approval.appr.model.request.authority.AuthorityClientRequest;
import com.msb.bpm.approval.appr.util.HeaderUtil;
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
public class UserManagementClient extends AbstractClient {
  private final String GET_USER = "getUser";
  private final RestTemplate restTemplate;
  private final UserClientProperties userClientProperties;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }


  public UserManagementClient(@Qualifier("userManagerRestTemplate") RestTemplate restTemplate, UserClientProperties userClientProperties) {
    {
      this.restTemplate = restTemplate;
      this.userClientProperties = userClientProperties;
    }
  }

  public GetUserResponse getUser() {
    log.info("getUser START with request={}");
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(HeaderUtil.getToken());
      String uri = UriComponentsBuilder.fromUriString(this.userClientProperties.getBaseUrl() + this.userClientProperties.getEndpoint().get(GET_USER)).toUriString();
      HttpEntity<AuthorityClientRequest> entity = new HttpEntity<>(null, headers);
      GetUserResponse response = super.exchange(uri, HttpMethod.GET, entity, GetUserResponse.class);

      log.info("getUser END with response={}", response);
      return response;
    } catch (Exception e) {
      log.error("getUser with -> Error:{} ", e);
      throw e;
    }
  }

  public String getSaleCode() {
    log.info("getSaleCode START: ");
    try {
      GetUserResponse response = getUser();
      if (response != null) {
        log.info("getSaleCode with response :{}", response);
        return response.getData().getEmployeeId();
      }
    } catch (Exception e) {
      log.error("getSaleCode Error:{}", e);
      return null;
    }
    return null;
  }
}
