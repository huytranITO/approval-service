package com.msb.bpm.approval.appr.client.bpm;


import com.google.gson.Gson;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.bpm.BpmOperationEndpoint;
import com.msb.bpm.approval.appr.model.request.bpm.operation.SynApplicationRequest;
import com.msb.bpm.approval.appr.model.response.bpm.operation.ClientResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@AllArgsConstructor
public class BpmOperationClient extends AbstractClient {
    private BpmClientConfigProperties bpmProperties;

    private final RestTemplate restTemplate;

    @Override
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public ClientResponse syncApplication(SynApplicationRequest request) {
        return syncApplication(BpmOperationEndpoint.SYNC_APPLICATION, request);
    }

    private ClientResponse syncApplication(BpmOperationEndpoint bpmOperationEndpoint, SynApplicationRequest request) {
        try {

            String endpoint = this.bpmProperties.getEndpoint().get(bpmOperationEndpoint.getValue()).getUrl();
            log.info("[CALL_API_BPM_OPERATION] call api {} request : {}", endpoint, new Gson().toJson(request));

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(HeaderUtil.getToken());
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<?> http = new HttpEntity<>(request, httpHeaders);

            String uri = UriComponentsBuilder
                    .fromUriString(this.bpmProperties.getBaseUrl() + endpoint)
                    .toUriString();

            ClientResponse response = getRestTemplate()
                    .exchange(uri, HttpMethod.POST, http, ClientResponse.class).getBody();

            log.info("[CALL_API_BPM_OPERATION] call api {} response : {}", endpoint, response);
            return response;
        } catch (HttpServerErrorException e) {
            log.error("method: exchange, HttpServerErrorException, httpStatus: {}, body: {}, error: {}"
                    , e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
            return ClientResponse.builder()
                    .code(String.valueOf(e.getRawStatusCode()))
                    .message(e.getMessage())
                    .data(e.getResponseBodyAsString())
                    .build();
        } catch (HttpClientErrorException e) {
            log.error("method: exchange, HttpClientErrorException, httpStatus: {}, body: {}, error: {}"
                    , e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
            return ClientResponse.builder()
                    .code(String.valueOf(e.getRawStatusCode()))
                    .message(e.getMessage())
                    .data(e.getResponseBodyAsString())
                    .build();
        } catch (ResourceAccessException e) {
            log.error("method: exchange, ResourceAccessException, httpStatus: {}, error: {}", HttpStatus.REQUEST_TIMEOUT, e.getMessage());
            return ClientResponse.builder()
                    .code(String.valueOf(HttpStatus.REQUEST_TIMEOUT.value()))
                    .message(e.getMessage())
                    .build();
        } catch (Exception exception) {
            log.error("method: syncApplication, error ", exception);
            return ClientResponse.builder()
                    .code(String.valueOf(HttpStatus.EXPECTATION_FAILED.value()))
                    .message(exception.getMessage())
                    .build();
        }
    }
}
