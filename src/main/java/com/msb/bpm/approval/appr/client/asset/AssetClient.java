package com.msb.bpm.approval.appr.client.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralConfigProperties;
import com.msb.bpm.approval.appr.enums.application.AssetBusinessType;
import com.msb.bpm.approval.appr.enums.asset.CollateralEndpoint;
import com.msb.bpm.approval.appr.model.request.asset.BPMAssetRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2CreateAssetRequest;
import com.msb.bpm.approval.appr.model.request.collateral.AssetResponse;
import com.msb.bpm.approval.appr.model.response.asset.AssetInfoResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@AllArgsConstructor
public class AssetClient extends AbstractClient {

  @Autowired
  @Qualifier("restTemplateCustomHandler")
  private RestTemplate restTemplate;
  private final AssetClientConfigProperties properties;
  private final CollateralConfigProperties collateralConfigProperties;
  private final ObjectMapper objectMapper;
  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public AssetInfoResponse getAssetData(String bpmId, AssetBusinessType assetBusinessType) {
    String endpoint = this.collateralConfigProperties
        .getEndpoint()
        .get(CollateralEndpoint.COMMON_GET_ASSET_BY_APPLICATION.getValue())
        .getUrl();

    log.info("[CALL_API_GET_ASSET_DATA] call api {} request : {}", endpoint, bpmId);

    String token = HeaderUtil.getToken();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBearerAuth(token);
    httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
    HttpEntity<?> http = new HttpEntity<>(httpHeaders);

    String uri = UriComponentsBuilder
        .fromUriString(this.collateralConfigProperties.getBaseUrl() + endpoint)
        .queryParam("applicationId", bpmId)
        .queryParam("businessType", assetBusinessType.getValue())
        .toUriString();
    AssetInfoResponse response = exchangeCustomHandler(uri, HttpMethod.GET, http, AssetInfoResponse.class);
    log.info("[CALL_API_GET_ASSET_DATA] call api {}, request : {}, response : {}", endpoint,
        JsonUtil.convertObject2String(bpmId, objectMapper),
        JsonUtil.convertObject2String(response, objectMapper));
    return response;
  }

  public AssetResponse cmsCreateAssetData(PostCmsV2CreateAssetRequest request) {
    String endpoint = this.properties
        .getEndpoint()
        .get(CollateralEndpoint.CREATE_CMS_ASSET.getValue())
        .getUrl();

    String uri = UriComponentsBuilder
        .fromUriString(this.properties.getBaseUrl() + endpoint)
        .toUriString();

    log.info("[CALL_API_CMS_CREATE_ASSET_DATA] call api {} request : {}", uri,
        JsonUtil.convertObject2String(request, objectMapper));

    String token = HeaderUtil.getToken();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBearerAuth(token);
    httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
    HttpEntity<?> http = new HttpEntity<>(request, httpHeaders);

    return exchangeCustomHandler(uri, HttpMethod.POST, http, AssetResponse.class);
  }

  public void bpmCreateAssetData(BPMAssetRequest request) {
    String endpoint = this.properties
        .getEndpoint()
        .get(CollateralEndpoint.CREATE_BPM_ASSET.getValue())
        .getUrl();

    String uri = UriComponentsBuilder
        .fromUriString(this.properties.getBaseUrl() + endpoint)
        .toUriString();

    log.info("[CALL_API_BPM_CREATE_ASSET_DATA] call api {} request : {}", uri,
        JsonUtil.convertObject2String(request, objectMapper));

    String token = HeaderUtil.getToken();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBearerAuth(token);
    httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
    HttpEntity<?> http = new HttpEntity<>(request, httpHeaders);

    exchangeCustomHandler(uri, HttpMethod.PUT, http, Void.class);
  }
}
