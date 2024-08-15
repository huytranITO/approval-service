package com.msb.bpm.approval.appr.client.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.model.response.configuration.MercuryDataResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.StringUtil;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class MercuryClient extends AbstractClient {
  private static final String GET_PLACE_BY_PARENT_CODE = "getPlaceByParentCode";
  private static final String METHOD = "method";
  private static final String PARENT_CODE = "parentCode";


  private final RestTemplate restTemplate;
  private final ApplicationConfig applicationConfig;
  private final ObjectMapper objectMapper;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public MercuryDataResponse searchPlace(String code) {
    try {
      log.info("searchPlace START with code={} ", code);
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<?> httpEntity = new HttpEntity<>(headers);

      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(
              this.applicationConfig.getClient().getMercury().getUrlBase()
                  + this.applicationConfig.getClient().getMercury().getUrlGetPlace())
          .queryParam(METHOD, GET_PLACE_BY_PARENT_CODE);
      if(StringUtils.isNotBlank(code)) {
        uriBuilder.queryParam(PARENT_CODE, code);
      }
      String uri = uriBuilder.toUriString();
      log.info("searchPlace call uri={}, with code={} ",uri, code);
      MercuryDataResponse response = exchange(uri, HttpMethod.GET, httpEntity,
          MercuryDataResponse.class);
      log.info("searchPlace END with code={}, response {}",
          uri, JsonUtil.convertObject2String(response, objectMapper));
      return response;

    } catch (Exception exception) {
      log.info("searchPlace END with code={}, error: {}",
          code, exception.getMessage());
      return null;
    }
  }
}
