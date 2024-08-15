package com.msb.bpm.approval.appr.client.collateral;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.asset.CollateralEndpoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.collateral.AssetAllocationRequest;
import com.msb.bpm.approval.appr.model.request.collateral.AssetResponse;
import com.msb.bpm.approval.appr.model.request.collateral.CreditAssetRequest;
import com.msb.bpm.approval.appr.model.request.collateral.GetAssetInfoRequest;
import com.msb.bpm.approval.appr.model.response.asset.AssetInfoResponse;
import com.msb.bpm.approval.appr.model.response.bpm.operation.DataBpmOperationResponse;
import com.msb.bpm.approval.appr.model.response.collateral.AssetRuleChecklistResponse;
import com.msb.bpm.approval.appr.model.response.collateral.CollateralClientResponse;
import com.msb.bpm.approval.appr.model.response.collateral.CreditAssetAllocationResponse;
import com.msb.bpm.approval.appr.model.response.collateral.OpRiskAssetResponse;
import com.msb.bpm.approval.appr.model.response.collateral.OpRiskAssetResponse.OpRiskAssetData;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class CollateralClient extends AbstractClient {

    private final RestTemplate restTemplate;
    private final CollateralConfigProperties configProperties;
    private final ObjectMapper objectMapper;

    private static final String BPM = "BPM";
    private final String SUCCESS_CODE = "HL-000";

    @Override
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public List<AssetRuleChecklistResponse> getAssetRuleChecklist(String bpmId) {
        try {
            String endpoint = this.configProperties
                    .getEndpoint()
                    .get(CollateralEndpoint.GET_ASSET_RULE_CHECKLIST.getValue())
                    .getUrl();

            log.info("[CALL_API_RUlE_CHECKLIST_ASSET] call api {} request : {}", endpoint, bpmId);

            String token = HeaderUtil.getToken();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<?> http = new HttpEntity<>(httpHeaders);

            String uri = UriComponentsBuilder
                    .fromUriString(this.configProperties.getBaseUrl() + endpoint + "/" + bpmId)
                    .toUriString();
            List<AssetRuleChecklistResponse> response = exchange(uri, HttpMethod.GET, http,
                    new ParameterizedTypeReference<List<AssetRuleChecklistResponse>>() {
                    });
            log.info("[CALL_API_RUlE_CHECKLIST_ASSET] call api {}, request : {}, response : {}", endpoint,
                    JsonUtil.convertObject2String(bpmId, objectMapper),
                    JsonUtil.convertObject2String(response, objectMapper));
            return response;
        } catch (Exception exception) {
            log.error("method: getRuleChecklistAsset with request={}, error: ", bpmId, exception);
            throw new ApprovalException(DomainCode.GET_ASSET_RULE_CHECKLIST_ERROR);
        }
    }

    public CreditAssetAllocationResponse getAssetAllocationInfo(String bpmId) {
        try {
            String endpoint = String.format(this.configProperties.getAllocationConfig().getViewAssetAllocation(), bpmId, BPM);

            log.info("[CALL_API_GET_ASSET_ALLOCATION_INFO] call api {} request : {}", endpoint, bpmId);

            String token = HeaderUtil.getToken();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<?> http = new HttpEntity<>(httpHeaders);

            String uri = UriComponentsBuilder
                    .fromUriString(this.configProperties.getBaseUrl() + endpoint)
                    .toUriString();
            CollateralClientResponse response = exchange(uri, HttpMethod.GET, http, CollateralClientResponse.class);
            log.info("[CALL_API_GET_ASSET_ALLOCATION_INFO] call api {}, request : {}, response : {}", endpoint,
                    JsonUtil.convertObject2String(bpmId, objectMapper),
                    JsonUtil.convertObject2String(response, objectMapper));
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) response.getData();
            return objectMapper.convertValue(data, CreditAssetAllocationResponse.class);
        } catch (Exception exception) {
            log.error("method: getAssetAllocationInfo with request={}, error: ", bpmId, exception);
            throw new ApprovalException(DomainCode.ALLOCATION_GET_ASSET_ERROR);
        }
    }

    public CreditAssetAllocationResponse updateAssetAllocationInfo(String bpmId, List<AssetAllocationRequest> request) {
        try {
            log.info("[CALL_API_UPDATE_ASSET_ALLOCATION] call api with id {} and request : {}", bpmId, JsonUtil.convertObject2String(request, objectMapper));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(HeaderUtil.getToken());
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<AssetAllocationRequest>> httpEntity = new HttpEntity<>(request, httpHeaders);

            String uri = UriComponentsBuilder
                    .fromUriString(this.configProperties.getBaseUrl() +
                            String.format(this.configProperties.getAllocationConfig().getSaveAssetAllocation(), bpmId, BPM))
                    .toUriString();
            CreditAssetAllocationResponse response = exchange(uri, HttpMethod.POST, httpEntity, CreditAssetAllocationResponse.class);
            log.info("[CALL_API_UPDATE_ASSET_ALLOCATION_INFO] with id {} and response : {}",bpmId, JsonUtil.convertObject2String(response, objectMapper));
            return response;
        } catch (Exception exception) {
            log.error("method: updateAssetAllocationInfo, error ", exception);
            throw new ApprovalException(DomainCode.ALLOCATION_SAVE_VALUATION_ERROR);
        }
    }

    public void refreshStatusApplicationDraft(String bpmId) {
        try {
            String endpoint = this.configProperties
                    .getEndpoint()
                    .get(CollateralEndpoint.APPLICATION_DRAFT_REFRESH_STATUS.getValue())
                    .getUrl();

            log.info("[CALL_API_APPLICATION_DRAFT_REFRESH_STATUS] call api {} request : {}", endpoint, bpmId);

            String token = HeaderUtil.getToken();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<?> http = new HttpEntity<>(httpHeaders);

            String uri = UriComponentsBuilder
                    .fromUriString(this.configProperties.getBaseUrl() + endpoint + "/" + bpmId)
                    .toUriString();
            CollateralClientResponse response = exchange(uri, HttpMethod.GET, http, CollateralClientResponse.class);
            log.info("[CALL_API_APPLICATION_DRAFT_REFRESH_STATUS] call api {}, request : {}, response : {}", endpoint,
                    JsonUtil.convertObject2String(bpmId, objectMapper),
                    JsonUtil.convertObject2String(response, objectMapper));
        } catch (Exception exception) {
            log.error("method: refreshStatusApplicationDraft with request={}, error: ", bpmId, exception);
        }
    }

    public boolean validOpr(String bpmId, String processingRole) {
        boolean isValid = false;
        try {
            String endpoint = this.configProperties
                    .getEndpoint()
                    .get(CollateralEndpoint.VALID_OPR.getValue())
                    .getUrl();

            log.info("[CALL_VALID_OPR] call api: {} request bpmId: {} processingRole: {}", endpoint, bpmId, processingRole);

            String token = HeaderUtil.getToken();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<?> http = new HttpEntity<>(httpHeaders);

            String uri = UriComponentsBuilder
                    .fromUriString(this.configProperties.getBaseUrl() + endpoint + "/" + bpmId)
                    .queryParam("processingRole", processingRole)
                    .toUriString();
            CollateralClientResponse response = exchange(uri, HttpMethod.GET, http, CollateralClientResponse.class);
            log.info("[CALL_VALID_OPR] call api {}, request : {}, response : {}", endpoint,
                    JsonUtil.convertObject2String(bpmId, objectMapper),
                    JsonUtil.convertObject2String(response, objectMapper));
            if (SUCCESS_CODE.equals(response.getCode())) {
                isValid = (boolean) response.getData();
            }
        } catch (Exception exception) {
            log.error("method: validOpr with request={}, error: ", bpmId, exception);
        }
        return isValid;
    }

    public CollateralClientResponse checkCollateralAssetStatus(String bpmId, int countCollateral) {
        try {
            String endpoint = this.configProperties
                    .getEndpoint()
                    .get(CollateralEndpoint.CHECK_APPLICATION_DRAFT.getValue())
                    .getUrl();

            log.info("[CALL_API_CHECK_COLLATERAL_ASSET_STATUS] call api {} request : {}", endpoint, bpmId);

            String token = HeaderUtil.getToken();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<?> http = new HttpEntity<>(httpHeaders);

            String uri = UriComponentsBuilder
                    .fromUriString(this.configProperties.getBaseUrl() + endpoint)
                    .queryParam("applicationId", bpmId)
                    .queryParam("countCollateral", countCollateral)
                    .toUriString();
            CollateralClientResponse response = exchange(uri, HttpMethod.GET, http, CollateralClientResponse.class);
            log.info("[CALL_API_CHECK_COLLATERAL_ASSET_STATUS] call api {}, request : {}, response : {}", endpoint,
                    JsonUtil.convertObject2String(bpmId, objectMapper),
                    JsonUtil.convertObject2String(response, objectMapper));
            return response;
        } catch (Exception exception) {
            log.error("method: checkCollateralAssetStatus with request={}, error: ", bpmId, exception);
        }
        return null;
    }

    public DataBpmOperationResponse getDataRequestBpmOperation(String bpmId) {
        try {
            String endpoint = this.configProperties
                    .getEndpoint()
                    .get(CollateralEndpoint.GET_REQUEST_DATA_OPERATION.getValue())
                    .getUrl();

            log.info("[CALL_API_GET_DATA_BPM_OPERATION] call api {} request : {}", endpoint, bpmId);

            String token = HeaderUtil.getToken();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<?> http = new HttpEntity<>(httpHeaders);

            String uri = UriComponentsBuilder
                    .fromUriString(this.configProperties.getBaseUrl() + endpoint)
                    .queryParam("applicationId", bpmId)
                    .toUriString();
            DataBpmOperationResponse response = exchange(uri, HttpMethod.GET, http, DataBpmOperationResponse.class);
            log.info("[CALL_API_GET_DATA_BPM_OPERATION STATUS] call api {}, request : {}, response : {}", endpoint,
                    JsonUtil.convertObject2String(bpmId, objectMapper),
                    JsonUtil.convertObject2String(response, objectMapper));
            return response;
        } catch (Exception exception) {
            log.error("method: getDataRequestBpmOperation with request={}, error: ", bpmId, exception);
        }
        return null;
    }

    public CollateralClientResponse getDataAssetFormTemplate(String bpmId) {
        try {
            String endpoint = this.configProperties
                    .getEndpoint()
                    .get(CollateralEndpoint.FORM_TEMPLATE.getValue())
                    .getUrl();

            log.info("[CALL_API_GET_DATA_ASSET_FORM_TEMPLATE] call api {} request : {}", endpoint, bpmId);

            String token = HeaderUtil.getToken();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<?> http = new HttpEntity<>(httpHeaders);

            String uri = UriComponentsBuilder
                    .fromUriString(this.configProperties.getBaseUrl() + endpoint)
                    .queryParam("applicationId", bpmId)
                    .toUriString();
            CollateralClientResponse response = exchange(uri, HttpMethod.GET, http, CollateralClientResponse.class);
            log.info("[CALL_API_GET_DATA_ASSET_FORM_TEMPLATE] call api {}, request : {}, response : {}", endpoint,
                    JsonUtil.convertObject2String(bpmId, objectMapper),
                    JsonUtil.convertObject2String(response, objectMapper));
            return response;
        } catch (Exception exception) {
            log.error("method: checkCollateralAssetStatus with request={}, error: ", bpmId, exception);
        }
        return null;
    }
    public List<CreditAssetRequest> mappingCreditAssetInfo(String bpmId, List<CreditAssetRequest> request) {
        try {
            log.info("[CALL_API_MAPPING_CREDIT_ASSET] call api with id: {} and request : {}", bpmId, JsonUtil.convertObject2String(request, objectMapper));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(HeaderUtil.getToken());
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<CreditAssetRequest>> httpEntity = new HttpEntity<>(request, httpHeaders);

            String uri = UriComponentsBuilder
                    .fromUriString(this.configProperties.getBaseUrl() +
                            String.format(this.configProperties.getAllocationConfig().getSaveAssets(), bpmId))
                    .toUriString();
            List<CreditAssetRequest> response = exchange(uri, HttpMethod.POST, httpEntity,
                    new ParameterizedTypeReference<List<CreditAssetRequest>>() {
            });
            log.info("[CALL_API_MAPPING_CREDIT_ASSET] response with id: {} and : {}", bpmId, JsonUtil.convertObject2String(response, objectMapper));
            return response;
        } catch (Exception exception) {
            log.error("method: mappingAssetAllocationInfo, error ", exception);
            throw new ApprovalException(DomainCode.ALLOCATION_SAVE_ASSET_ERROR);
        }
    }
    public List<OpRiskAssetData> getOpRiskAsset(String bpmId) {
        log.info("[COLLATERAL_SERVICE] CollateralClient.getOpRiskAsset START with bpmId=[{}]", bpmId);
        List<OpRiskAssetData> response = new ArrayList<>();
        String uri = UriComponentsBuilder
            .fromUriString(configProperties.getBaseUrl() +
                configProperties.getEndpoint().get(CollateralEndpoint.GET_OPR_ASSET.getValue()).getUrl())
            .queryParam("applicationId", bpmId)
            .toUriString();
        HttpHeaders headers = buildCommonHeaders(HeaderUtil.getToken());
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        OpRiskAssetResponse opRiskAssetResponse = exchange(uri, HttpMethod.GET, entity, OpRiskAssetResponse.class);
        if (ObjectUtils.isEmpty(opRiskAssetResponse) || CollectionUtils.isEmpty(opRiskAssetResponse.getData())) {
            return Collections.emptyList();
        }
        log.info("[COLLATERAL_SERVICE] CollateralClient.getOpRiskAsset END with response=[{}]", response);
        return opRiskAssetResponse.getData();
    }

    public <T> T callApiCommonAsset(Object request, HttpMethod method, String endpoint, Class<T> responseType) {
        try {
            log.info("[CALL_API_COMMON_ASSET_INFO] call api {} with method: {} and request : {}", endpoint, method,
                    JsonUtil.convertObject2String(request, objectMapper));

            HttpHeaders httpHeaders = buildCommonHeaders(HeaderUtil.getToken());
            HttpEntity<?> http;
            if(ObjectUtils.isEmpty(request)) {
                http = new HttpEntity<>(httpHeaders);
            } else {
                http = new HttpEntity<>(request, httpHeaders);
            }

            return exchangeCustomHandler(endpoint, method, http, responseType);
        } catch (Exception exception) {
            log.error("method: [CALL_API_COMMON_ASSET_INFO], error ", exception);
            if(exception instanceof  HttpClientErrorException) {
                HttpClientErrorException ex = (HttpClientErrorException) exception;
                AssetResponse response = JsonUtil.convertString2Object(ex.getResponseBodyAsString(), AssetResponse.class, objectMapper);
                if(ObjectUtils.isEmpty(response) || ObjectUtils.isEmpty(response.getMessage())) {
                    throw new ApprovalException(DomainCode.ASSET_ERROR, new Object[]{ex.getResponseBodyAsString()});
                }
                if(ObjectUtils.isNotEmpty(response)
                        && "HL-001".equals(response.getCode())
                        && ObjectUtils.isNotEmpty(response.getMessage())) {
                    throw new ApprovalException(DomainCode.ASSET_INVALID_ERROR, new Object[]{response.getMessage().getVi()});
                }
            }
            throw new ApprovalException(DomainCode.ASSET_ERROR, new Object[]{"Lỗi tài sản"});
        }
    }

    public AssetInfoResponse getCommonAssetInfo(String bpmId, String businessType) {
        try {
            String endpoint = this.configProperties
                .getEndpoint()
                .get(CollateralEndpoint.COMMON_GET_ASSET_BY_APPLICATION.getValue())
                .getUrl();

            log.info("[CALL_API_GET_COMMON_ASSET_INFO] call api {} request : {}", endpoint, bpmId);

            String token = HeaderUtil.getToken();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<?> http = new HttpEntity<>(httpHeaders);

            String uri = UriComponentsBuilder
                .fromUriString(this.configProperties.getBaseUrl() + endpoint)
                .queryParam("applicationId", bpmId)
                .queryParam("businessType", businessType)
                .toUriString();
            AssetInfoResponse response = exchange(uri, HttpMethod.GET, http, AssetInfoResponse.class);
            log.info("[CALL_API_GET_COMMON_ASSET_INFO STATUS] call api {}, request : {}, response : {}", endpoint,
                JsonUtil.convertObject2String(bpmId, objectMapper),
                JsonUtil.convertObject2String(response, objectMapper));
            return response;
        } catch (Exception exception) {
            log.error("method: getCommonAssetInfo with request={}, error: ", bpmId, exception);
        }
        return null;
    }

    public AssetInfoResponse getAssetInfo(GetAssetInfoRequest request) {
        try {
            String endpoint = this.configProperties
                .getEndpoint()
                .get(CollateralEndpoint.GET_ASSET_INFO.getValue())
                .getUrl();

            log.info("[CALL_API_GET_ASSET_INFO] call api {} request : {}", endpoint, JsonUtil.convertObject2String(request, objectMapper));

            String token = HeaderUtil.getToken();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<?> http = new HttpEntity<>(request, httpHeaders);

            String uri = UriComponentsBuilder
                .fromUriString(this.configProperties.getBaseUrl() + endpoint)
                .toUriString();
            Object response = exchange(uri, HttpMethod.POST, http, AssetInfoResponse.class);
            log.info("[CALL_API_GET_ASSET_INFO STATUS] call api {}, request : {}, response : {}", endpoint,
                JsonUtil.convertObject2String(request, objectMapper),
                JsonUtil.convertObject2String(response, objectMapper));
            return (AssetInfoResponse) response;
        } catch (Exception exception) {
            log.error("method: getAssetInfo with request={}, error: ", JsonUtil.convertObject2String(request, objectMapper) , exception);
        }
        return null;
    }
}
