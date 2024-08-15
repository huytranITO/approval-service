package com.msb.bpm.approval.appr.client.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.chat.SmartChatEndpoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.chat.AddUserToRoomRequest;
import com.msb.bpm.approval.appr.model.request.chat.RoomRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@AllArgsConstructor
public class SmartChatClient extends AbstractClient {

    private final RestTemplate restTemplate;
    private final SmartChatConfigProperties configProperties;
    private final ObjectMapper objectMapper;

    @Override
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public ApiResponse createRoom(RoomRequest request) {
        try {
            String endpoint = this.configProperties
                    .getEndpoint()
                    .get(SmartChatEndpoint.CREATE_GROUP.getValue())
                    .getUrl();

            log.info("[CALL_API_CREATE_GROUP] call api {} with request : {}", endpoint, JsonUtil.convertObject2String(request, objectMapper));

            HttpHeaders httpHeaders = getHttpHeaders();
            HttpEntity<RoomRequest> httpEntity = new HttpEntity<>(request, httpHeaders);
            String uri = UriComponentsBuilder
                    .fromUriString(this.configProperties.getBaseUrl() + endpoint)
                    .toUriString();
            ApiResponse response = exchange(uri, HttpMethod.POST, httpEntity, ApiResponse.class);
            log.info("[CALL_API_CREATE_GROUP] call api {} returns response : {}", endpoint,
                    JsonUtil.convertObject2String(response, objectMapper));
            return response;
        } catch (Exception exception) {
            log.error("method: createRoom with request={}, error: ", JsonUtil.convertObject2String(request, objectMapper), exception);
            throw new ApprovalException(DomainCode.INTERNAL_SERVICE_ERROR);
        }
    }

    public ApiResponse closeRoom(String roomName) {
        try {
            String endpoint = this.configProperties
                    .getEndpoint()
                    .get(SmartChatEndpoint.CLOSE_GROUP.getValue())
                    .getUrl();

            log.info("[CALL_API_CLOSE_GROUP] call api {} request : {}", endpoint, roomName);

            HttpHeaders httpHeaders = getHttpHeaders();
            HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
            String uri = UriComponentsBuilder
                    .fromUriString(this.configProperties.getBaseUrl() + endpoint).queryParam("groupName", roomName)
                    .toUriString();
            ApiResponse response = exchange(uri, HttpMethod.POST, httpEntity, ApiResponse.class);
            log.info("[CALL_API_CLOSE_GROUP] call api {} request : {}, response : {}", endpoint, roomName, JsonUtil.convertObject2String(response, objectMapper));
            return response;
        } catch (Exception exception) {
            log.error("method: closeRoom with request={}, error: ", roomName, exception);
            throw new ApprovalException(DomainCode.INTERNAL_SERVICE_ERROR);
        }
    }

    public ApiResponse addUserToRoom(AddUserToRoomRequest request) {
        try {
            String endpoint = this.configProperties
                    .getEndpoint()
                    .get(SmartChatEndpoint.ADD_USER_TO_ROOM.getValue())
                    .getUrl();

            log.info("[CALL_API_ADD_USER_TO_ROOM] call api {} with request : {}", endpoint, JsonUtil.convertObject2String(request, objectMapper));

            HttpHeaders httpHeaders = getHttpHeaders();
            HttpEntity<AddUserToRoomRequest> httpEntity = new HttpEntity<>(request, httpHeaders);
            String uri = UriComponentsBuilder
                    .fromUriString(this.configProperties.getBaseUrl() + endpoint)
                    .toUriString();
            ApiResponse response = exchange(uri, HttpMethod.POST, httpEntity, ApiResponse.class);
            log.info("[CALL_API_ADD_USER_TO_ROOM] call api {} returns response : {}", endpoint,
                    JsonUtil.convertObject2String(response, objectMapper));
            return response;
        } catch (Exception exception) {
            log.error("method: addUserToRoom with request={}, error: ", JsonUtil.convertObject2String(request, objectMapper), exception);
            throw new ApprovalException(DomainCode.INTERNAL_SERVICE_ERROR);
        }
    }

    @NotNull
    private HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(HeaderUtil.getToken());
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return httpHeaders;
    }
}
