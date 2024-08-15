package com.msb.bpm.approval.appr.client.configuration;

import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.config.ConfigurationClientProperties;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.model.request.configuration.GetCategoryConditionRequest;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryConditionResponse;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.model.response.configuration.ConfigurationBaseResponse;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.ParameterizedTypeReference;
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
public class ConfigurationListClient extends AbstractClient {

  private static final String GET_DATA_BY_CATEGORY_CODE = "getCategoryDataByCategoryCode";
  private static final String GET_CATEGORY_DATA_BY_CODE = "getCategoryDataByCodes";
  private static final String GET_CATEGORY_CONDITION = "getCategoryCondition";

  private static final String CODE = "code";

  private final RestTemplate restTemplate;
  private final ConfigurationClientProperties configurationClientProperties;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public List<CategoryDataResponse> findByCategoryCode(
      ConfigurationCategory configurationCategory, String token) {
    if (configurationCategory == null) {
      return new ArrayList<>();
    }

    try {
      HttpEntity<Objects> httpEntity = createHeaderWithTokenUser(token);
      String endpoint = this.configurationClientProperties.getEndpoint()
          .get(GET_DATA_BY_CATEGORY_CODE);

      String uri = UriComponentsBuilder.fromUriString(
              this.configurationClientProperties.getBaseUrl()
                  + endpoint)
          .queryParam(CODE, configurationCategory.getCode())
          .toUriString();
      log.info("[CALL_API][CONFIGURATION_LIST] ENDPOINT {} ", uri);
      List<CategoryDataResponse> responses = exchange(uri, HttpMethod.GET, httpEntity,
          new ParameterizedTypeReference<List<CategoryDataResponse>>() {
          });

      log.info("[CALL_API][CONFIGURATION_LIST] ENDPOINT {}, response {}",
          uri, responses);
      return responses;

    } catch (Exception exception) {
      log.error("method: findByCategoryCode, error ", exception);
      return new ArrayList<>();
    }

  }

  public GetListResponse findByListCategoryDataCodes(
      List<String> listCode, String token) {
    if (CollectionUtils.isEmpty(listCode)) {
      return GetListResponse.builder().build();
    }

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, token);
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<List<String>> httpEntity = new HttpEntity<>(listCode, headers);

      String endpoint = this.configurationClientProperties.getEndpoint()
          .get(GET_CATEGORY_DATA_BY_CODE);

      String uri = UriComponentsBuilder.fromUriString(
              this.configurationClientProperties.getBaseUrl()
                  + endpoint)
          .toUriString();
      log.info("[CALL_API][CONFIGURATION_LIST] ENDPOINT [{}] ", uri);
      GetListResponse responses = exchange(uri, HttpMethod.POST, httpEntity,
          new ParameterizedTypeReference<GetListResponse>() {
          });

      log.info("[CALL_API][CONFIGURATION_LIST] ENDPOINT {}, response {}",
          uri, responses);
      return responses;

    } catch (Exception exception) {
      log.error("method: findByCategoryData, error ", exception);
      return GetListResponse.builder().build();
    }

  }

  public ConfigurationBaseResponse<CategoryConditionResponse> findCategoryCondition(GetCategoryConditionRequest request) {

      HttpEntity<?> httpEntity = new HttpEntity<>(request, buildCommonHeaders(HeaderUtil.getToken()));
      String endpoint = this.configurationClientProperties.getEndpoint()
          .get(GET_CATEGORY_CONDITION);

      String uri = UriComponentsBuilder.fromUriString(
              this.configurationClientProperties.getBaseUrl()
                  + endpoint)
          .toUriString();
      log.info("[CALL_API][CONFIGURATION_LIST] ENDPOINT {} ", uri);
    ConfigurationBaseResponse<CategoryConditionResponse> responses = exchange(uri, HttpMethod.POST, httpEntity,
          new ParameterizedTypeReference<ConfigurationBaseResponse<CategoryConditionResponse>>() {
          });

      log.info("[CALL_API][CONFIGURATION_LIST] ENDPOINT {}, response {}",
          uri, responses);
      return responses;
  }
  protected HttpEntity<Objects> createHeaderWithTokenUser(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, token);
    return new HttpEntity<>(headers);
  }
}
