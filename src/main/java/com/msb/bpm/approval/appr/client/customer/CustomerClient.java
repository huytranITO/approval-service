package com.msb.bpm.approval.appr.client.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.client.customer.request.AddOrUpdateCusRelationshipRequest;
import com.msb.bpm.approval.appr.client.customer.request.CommonMigrateVersionRequest;
import com.msb.bpm.approval.appr.client.customer.request.SearchCusRelationshipRequest;
import com.msb.bpm.approval.appr.client.customer.response.CommonMigrateVersionResponse;
import com.msb.bpm.approval.appr.client.customer.response.CusRelationForSearchResponse;
import com.msb.bpm.approval.appr.client.customer.response.CustomerRelationResponse;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.model.request.customer.CommonCustomerRequest;
import com.msb.bpm.approval.appr.model.request.customer.FindByListCustomerRequest;
import com.msb.bpm.approval.appr.model.request.customer.SearchByListCustomerRequest;
import com.msb.bpm.approval.appr.model.request.customereb.CreateCustomerEbRequest;
import com.msb.bpm.approval.appr.model.response.customer.CreateRBCustomerResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomerBaseResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse;
import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerResponse;
import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerV2Response;
import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerV3Response;
import com.msb.bpm.approval.appr.model.response.customer.UpdateRBCustomerResponse;
import com.msb.bpm.approval.appr.model.response.customereb.CustomerEbResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.ObjectMapperUtil;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 10/7/2023, Monday
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerClient extends AbstractClient {

  @Autowired
  @Qualifier("restTemplateCustomHandler")
  private RestTemplate restTemplate;

  private final ApplicationConfig applicationConfig;

  private final ObjectMapper objectMapper;

  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  private <T> void mapResponse(CustomerBaseResponse<T> result, Class<T> responseType) {
    String dataString = JsonUtil.convertObject2String(result.getData(), objectMapper);
    T data = JsonUtil.convertString2Object(dataString, responseType, objectMapper);
    result.setData(data);
  }

  public CustomerBaseResponse<SearchCustomerResponse> searchCustomer(String identityNumber) {
    CustomerBaseResponse<SearchCustomerResponse> result = call("search", identityNumber,
        CustomerBaseResponse.class, HttpMethod.GET);
    mapResponse(result, SearchCustomerResponse.class);
    return result;
  }
  public CustomerBaseResponse<CustomersResponse> searchCustomerByList(SearchByListCustomerRequest request) {
    CustomerBaseResponse<CustomersResponse> result = call("searchByList", request,
        CustomerBaseResponse.class, HttpMethod.POST);
        mapResponse(result, CustomersResponse.class);
    return result;
  }

  public CustomerBaseResponse<CreateRBCustomerResponse> createCustomer(
      CommonCustomerRequest request) {
    CustomerBaseResponse<CreateRBCustomerResponse> result = call("create", request,
        CustomerBaseResponse.class, HttpMethod.POST);
    mapResponse(result, CreateRBCustomerResponse.class);
    return result;
  }

  public CustomerBaseResponse<UpdateRBCustomerResponse> updateCustomer(
      CommonCustomerRequest request) {
    CustomerBaseResponse<UpdateRBCustomerResponse> result = call("update", request,
        CustomerBaseResponse.class, HttpMethod.PUT);
    mapResponse(result, UpdateRBCustomerResponse.class);
    return result;
  }
  public CustomerBaseResponse<CusRelationForSearchResponse> searchCustomerRelationship(
      SearchCusRelationshipRequest request) {
    CustomerBaseResponse<CusRelationForSearchResponse> result = call("searchRelationship", request,
        CustomerBaseResponse.class, HttpMethod.POST);
    mapResponse(result, CusRelationForSearchResponse.class);
    return result;
  }

  public CustomerBaseResponse<SearchCustomerV3Response> findByListCustomerCommon(
          FindByListCustomerRequest request) {
    CustomerBaseResponse<SearchCustomerV3Response> result = call("findByList", request,
            CustomerBaseResponse.class, HttpMethod.POST);
    mapResponse(result, SearchCustomerV3Response.class);
    return result;
  }

  public CustomerBaseResponse<CustomerRelationResponse> createCustomerRelationship(
      AddOrUpdateCusRelationshipRequest request) {
    CustomerBaseResponse<CustomerRelationResponse> result = call("addOrUpdateRelationship", request,
        CustomerBaseResponse.class, HttpMethod.POST);
    mapResponse(result, CustomerRelationResponse.class);
    return result;
  }
  public CustomerBaseResponse<CustomerRelationResponse> updateCustomerRelationship(
      AddOrUpdateCusRelationshipRequest request) {
    CustomerBaseResponse<CustomerRelationResponse> result = call("addOrUpdateRelationship", request,
        CustomerBaseResponse.class, HttpMethod.PUT);
    mapResponse(result, CustomerRelationResponse.class);
    return result;
  }
  public CustomerBaseResponse<CustomerEbResponse> createCustomerEb(
      CreateCustomerEbRequest request) {
    CustomerBaseResponse<CustomerEbResponse> result = call("createCustomerEb", request,
        CustomerBaseResponse.class, HttpMethod.POST);
    mapResponse(result, CustomerEbResponse.class);
    return result;
  }

  private <T> T call(String type, Object request, Class<T> responseType, HttpMethod method) {
    log.info("[CALL_API_CUSTOMER] execute with type={}, request={}", type,
        JsonUtil.convertObject2String(request, objectMapper));

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    String token = HeaderUtil.getToken();
    headers.setBearerAuth(token);
    HttpEntity<Object> entity = new HttpEntity<>(request, headers);
    String idPath = "";
    if (type.equalsIgnoreCase("update")) {
      idPath = "/" + ((CommonCustomerRequest)request).getCustomer().getId();
    }
    String uri = UriComponentsBuilder
        .fromUriString(applicationConfig.getClient().getCustomer().getUrlBase()
            + applicationConfig.getClient().getCustomer().getUrlCustomerRb() + idPath)
        .toUriString();
    if (type.equalsIgnoreCase("search")) {
      uri = UriComponentsBuilder
          .fromUriString(applicationConfig.getClient().getCustomer()
              .getUrlBase() + applicationConfig.getClient().getCustomer().getUrlCustomerRb())
          .queryParam("identityNumber", request)
          .toUriString();
      entity = new HttpEntity<>(headers);
    }
    if (type.equalsIgnoreCase("searchByList")) {
      uri = UriComponentsBuilder
          .fromUriString(applicationConfig.getClient().getCustomer().getUrlBase() +
              applicationConfig.getClient().getCustomer().getUrlCustomerRbByList())
          .toUriString();
    }
    if (type.equalsIgnoreCase("findByList")) {
      uri = UriComponentsBuilder
              .fromUriString(applicationConfig.getClient().getCustomer().getUrlBase() +
                      applicationConfig.getClient().getCustomer().getUrlFindByList())
              .toUriString();
      log.info("Search customer findByList with uri {}", String.valueOf(uri));
    }
    if (type.equalsIgnoreCase("create")) {
      uri = UriComponentsBuilder
          .fromUriString(applicationConfig.getClient().getCustomer().getUrlBase() +
              applicationConfig.getClient().getCustomer().getUrlCustomerRbMigrateVersion())
          .toUriString();
    }
    if (type.equalsIgnoreCase("searchRelationship")) {
      uri = UriComponentsBuilder
          .fromUriString(applicationConfig.getClient().getCustomer().getUrlBase() +
              applicationConfig.getClient().getCustomer().getUrlCustomerRelationshipDetail())
          .toUriString();
    }
    if (type.equalsIgnoreCase("addOrUpdateRelationship")) {
      uri = UriComponentsBuilder
          .fromUriString(applicationConfig.getClient().getCustomer().getUrlBase() +
              applicationConfig.getClient().getCustomer().getUrlCustomerRelationship())
          .toUriString();
    }
    if (type.equalsIgnoreCase("createCustomerEb")) {
      uri = UriComponentsBuilder
          .fromUriString(applicationConfig.getClient().getCustomer().getUrlBase() +
              applicationConfig.getClient().getCustomer().getUrlCreateCustomerEb())
          .toUriString();
    }
    T response = exchangeCustomHandler(uri, method, entity, responseType);
    log.info("[CALL_API_CUSTOMER] execute with type={}, request={}, response={}", type,
        JsonUtil.convertObject2String(request, objectMapper),
        JsonUtil.convertObject2String(response, objectMapper));
    return response;
  }

  /**
   * Client migrate customer version
   *
   * @param request
   * @return
   */
  public CommonMigrateVersionResponse migrateCustomerVersion(CommonMigrateVersionRequest request) {
    HttpHeaders httpHeaders = buildCommonHeaders(HeaderUtil.getToken());

    HttpEntity<CommonMigrateVersionRequest> entity = new HttpEntity<>(request, httpHeaders);

    String uri = UriComponentsBuilder
        .fromUriString(applicationConfig.getClient().getCustomer().getUrlBase()
            + applicationConfig.getClient().getCustomer().getUrlCustomerMigrateVersion())
        .toUriString();

    log.info("[CALL_API_MIGRATE_CUSTOMER] execute with api : [{}] , request : [{}]", uri, JsonUtil.convertObject2String(request, objectMapper));

    CommonMigrateVersionResponse response = exchange(uri, HttpMethod.PUT, entity, CommonMigrateVersionResponse.class);

    log.info("[CALL_API_MIGRATE_CUSTOMER] response : [{}]", JsonUtil.convertObject2String(response, objectMapper));

    return response;
  }

  public CustomerBaseResponse<SearchCustomerV2Response> searchCustomerDetail(Long refCustomerId,
      String version) {
    HttpHeaders httpHeaders = buildCommonHeaders(HeaderUtil.getToken());

    HttpEntity entity = new HttpEntity<>(httpHeaders);
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(
        applicationConfig.getClient().getCustomer().getUrlBase()
            + applicationConfig.getClient().getCustomer().getUrlCustomerDetail() + "/"
            + refCustomerId);
    if (StringUtils.isNotEmpty(version)) {
      uriComponentsBuilder.queryParam("version", version);
    }

    objectMapper.registerModule(ObjectMapperUtil.javaTimeModule());
    log.info("[searchCustomerDetail] execute with api : [{}] , request : [{}]",
        uriComponentsBuilder.toUriString(),
        "refCustomerId:" + refCustomerId + ", version:" + version);

    CustomerBaseResponse<SearchCustomerV2Response> response = exchange(
        uriComponentsBuilder.toUriString(), HttpMethod.GET,
        entity, CustomerBaseResponse.class);

    log.info("[searchCustomerDetail] response : [{}]",
        JsonUtil.convertObject2String(response, objectMapper));
    mapResponse(response, SearchCustomerV2Response.class);

    return response;
  }
}
