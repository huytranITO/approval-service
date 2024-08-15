package com.msb.bpm.approval.appr.client.general;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.general.GeneralEndpoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.response.general.GeneralInfoResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class GeneralInfoClient extends AbstractClient {

    public static final String PROGRAM = "program";
    public static final String RISK = "RISK";

    private final RestTemplate restTemplate;
    private final GeneralInfoConfigProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public List<GeneralInfoResponse> getGeneralInfo() {
        try {
            String endpoint = this.properties
                    .getEndpoint()
                    .get(GeneralEndpoint.GET_GENERAL_INFO.getValue())
                    .getUrl();

            log.info("[CALL_API_GENERAL_INFO] call api {} request : {}", endpoint, RISK);

            String token = HeaderUtil.getToken();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<?> http = new HttpEntity<>(httpHeaders);

            String uri = UriComponentsBuilder
                    .fromUriString(this.properties.getBaseUrl() + endpoint + "/")
                    .queryParam(PROGRAM, RISK)
                    .toUriString();
            List<GeneralInfoResponse> response = exchange(uri, HttpMethod.GET, http,
                    new ParameterizedTypeReference<List<GeneralInfoResponse>>() {
                    });
            log.info("[CALL_API_GENERAL_INFO] call api {}, request : {}, response : {}", endpoint,
                    JsonUtil.convertObject2String(PROGRAM, objectMapper),
                    JsonUtil.convertObject2String(response, objectMapper));
            return response;
        } catch (Exception exception) {
            log.error("method: general info with request={}, error: ", PROGRAM, exception);
            if (Objects.equals(DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR,
                    ((ApprovalException)exception).getCode())) {
                throw exception;
            }
            return new ArrayList<>();
        }
    }
}
