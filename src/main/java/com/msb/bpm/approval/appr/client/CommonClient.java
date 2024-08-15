package com.msb.bpm.approval.appr.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@AllArgsConstructor
public class CommonClient extends AbstractClient{
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public <T> T callApiCommon(Object request,
                               HttpMethod method,
                               String endpoint,
                               Class<T> responseType) {
        try {
            log.info("[callApiCommon] call api {} with method: {} and request : {}", endpoint, method,
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
            log.error("method: callApiCommon, error ", exception);
            throw new ApprovalException(DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR, new Object[]{"Lỗi lấy thông tin"});
        }
    }
}
