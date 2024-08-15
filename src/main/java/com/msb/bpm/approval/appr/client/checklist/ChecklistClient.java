package com.msb.bpm.approval.appr.client.checklist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.config.card.response.CheckClientResponse;
import com.msb.bpm.approval.appr.enums.checklist.EndPoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.dto.checklist.FileDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.request.checklist.CreateChecklistRequest;
import com.msb.bpm.approval.appr.model.request.checklist.DeleteChecklistGroupRequest;
import com.msb.bpm.approval.appr.model.request.checklist.GetChecklistRequest;
import com.msb.bpm.approval.appr.model.request.checklist.UpdateAdditionalDataRequest;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.checklist.PDGroupResponse;
import com.msb.bpm.approval.appr.model.response.checklist.SearchResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * @created : 30/5/2023, Monday
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ChecklistClient extends AbstractClient {

  private final RestTemplate restTemplate;

  private final ApplicationConfig applicationConfig;

  private final ObjectMapper objectMapper;


  @Override
  protected RestTemplate getRestTemplate() {
    return this.restTemplate;
  }

  public ChecklistBaseResponse<GroupChecklistDto> getChecklistByRequestCode(String requestCode){
    log.info("getChecklistByRequestCode START with getByRequestCode={}", requestCode);
    ChecklistBaseResponse<GroupChecklistDto> result = null;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<?> entity = new HttpEntity<>(headers);
      String uri = UriComponentsBuilder
              .fromUriString(applicationConfig.getClient().getChecklist().getUrlBase()
                      + applicationConfig.getClient().getChecklist().getEndPoint().get(EndPoint.
                      GET_BY_REQUEST_CODE.getValue()).getUrl()).path("/" + requestCode).toUriString();
      result = exchange(uri, HttpMethod.GET, entity, ChecklistBaseResponse.class);
      mapResponse(result, GroupChecklistDto.class);
    }
    catch (Exception ex) {
      throwEx(ex, DomainCode.GET_BY_REQUEST_CODE, requestCode);
    }
    log.info("getByRequestCode END with requestCode={}, result={}",
            requestCode, JsonUtil.convertObject2String(result, objectMapper));
    return result;
  }

  public ChecklistBaseResponse<GroupChecklistDto> uploadFileChecklist(CreateChecklistRequest request) {
    log.info("saveChecklist START with request={}", JsonUtil.convertObject2String(request, objectMapper));
    ChecklistBaseResponse<GroupChecklistDto> result = null;
    try {
      result = call(EndPoint.UPLOAD_FILE, request, ChecklistBaseResponse.class, HttpMethod.POST);
      mapResponse(result, GroupChecklistDto.class);
    }
    catch (Exception ex) {
      throwEx(ex, DomainCode.SAVE_CHECKLIST_ERROR, request.getRequestCode());
    }
    log.info("saveChecklist END with request=[{}], result={}",
        JsonUtil.convertObject2String(request, objectMapper), result);
    return result;
  }

  public ChecklistBaseResponse<GroupChecklistDto> uploadFileChecklistInternal(CreateChecklistRequest request, String basicAuthentication) {
    log.info("saveChecklist Internal START with request={}", JsonUtil.convertObject2String(request, objectMapper));
    ChecklistBaseResponse<GroupChecklistDto> result = null;
    try {
      result = callAsBasicAuth(EndPoint.UPLOAD_FILE_INTERNAL, request, ChecklistBaseResponse.class, HttpMethod.POST, basicAuthentication);
      mapResponse(result, GroupChecklistDto.class);
    }
    catch (Exception ex) {
      throwEx(ex, DomainCode.SAVE_CHECKLIST_ERROR, request.getRequestCode());
    }
    log.info("saveChecklist END with request=[{}], result={}",
        JsonUtil.convertObject2String(request, objectMapper), result);
    return result;
  }


//  @Cacheable
  public ChecklistBaseResponse<GroupChecklistDto> generateChecklist(GetChecklistRequest request) {
    ChecklistBaseResponse<GroupChecklistDto> result = null;
    try {
      result = call(EndPoint.GENERATE_CHECKLIST, request, ChecklistBaseResponse.class, HttpMethod.POST);
      mapResponse(result, GroupChecklistDto.class);
    }
    catch (Exception ex) {
      throwEx(ex, DomainCode.GENERATE_CHECKLIST_ERROR, request.getRequestCode());
    }
    return result;
  }


/*  @Cacheable*/
  public ChecklistBaseResponse<GroupChecklistDto> getMaster(GetChecklistRequest request) {
    log.info("get master START with request=[{}]", request);
    ChecklistBaseResponse<GroupChecklistDto> result = null;
    try {
      result = call(EndPoint.GET_MASTER, request, ChecklistBaseResponse.class, HttpMethod.POST);
      mapResponse(result, GroupChecklistDto.class);
    }
    catch (Exception ex) {
      throwEx(ex, DomainCode.GENERATE_CHECKLIST_ERROR, request.getRequestCode());
    }
    log.info("generateChecklist END with request=[{}], result=[{}]", request, result);
    return result;
  }

  public ChecklistBaseResponse<GroupChecklistDto> reload(GetChecklistRequest request) {
    ChecklistBaseResponse<GroupChecklistDto> result = null;
    try {
      result = call(EndPoint.RELOAD, request, ChecklistBaseResponse.class, HttpMethod.POST);
      mapResponse(result, GroupChecklistDto.class);
    }
    catch (Exception ex) {
      throwEx(ex, DomainCode.RELOAD_CHECKLIST_ERROR, request.getRequestCode());
    }
    return result;
  }

  private <T> void mapResponse(ChecklistBaseResponse<T> result, Class<T> responseType) {
    String dataString = JsonUtil.convertObject2String(result.getData(), objectMapper);
    T data = JsonUtil.convertString2Object(dataString, responseType, objectMapper);
    result.setData(data);
  }

  public void updateAdditionalData(UpdateAdditionalDataRequest request) {
    log.info("updateAdditionalData START with request=[{}]", request);
    try {
      call(EndPoint.ADDITIONAL_DATA, request, Object.class, HttpMethod.PUT);
    }
    catch (Exception ex) {
      throwEx(ex, DomainCode.ADDITIONAL_DATA_ERROR, request.getApplicationId());
    }
  }

  public void deleteChecklistGroup(DeleteChecklistGroupRequest request) {
    log.info("deleteChecklist START with request=[{}]", request);
    try {
      call(EndPoint.DELETE_CHECKLIST_GROUP, request, Object.class, HttpMethod.PUT);
    }
    catch (Exception ex) {
      throwEx(ex, DomainCode.DELETE_CHECKLIST_GROUP_ERROR, request.getApplicationId());
    }
  }

  public void updateChecklistVersion(Long applicationId) {
    log.info("updateChecklistVersion START with applicationId=[{}]", applicationId);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<?> entity = new HttpEntity<>(headers);
      Map<String, Object> pathVariable = new HashMap<>();
      pathVariable.put("applicationId", applicationId);
      String uri = UriComponentsBuilder
          .fromUriString(applicationConfig.getClient().getChecklist().getUrlBase()
              + applicationConfig.getClient().getChecklist().getEndPoint().get(EndPoint.
              UPDATE_CHECKLIST_VERSION.getValue()).getUrl()).uriVariables(pathVariable).toUriString();
      exchange(uri, HttpMethod.PUT, entity, ChecklistBaseResponse.class);
      log.info("updateChecklistVersion END with applicationId=[{}]", applicationId);
    }
    catch (Exception ex) {
      log.info("updateChecklistVersion with applicationId=[{}], error:",applicationId, ex);
      throwEx(ex, DomainCode.UPDATE_CHECKLIST_VERSION_ERROR, applicationId);
    }
  }

  public ChecklistBaseResponse<List<FileDto>> getHistoryFile(Long checkListMappingId) {
    log.info("getHistoryFile START with checkListMappingId=[{}]", checkListMappingId);
    ChecklistBaseResponse<List<FileDto>> result = null;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<?> entity = new HttpEntity<>(headers);
      Map<String, Object> pathVariable = new HashMap<>();
      pathVariable.put("checkListMappingId", checkListMappingId);
      String uri = UriComponentsBuilder
          .fromUriString(applicationConfig.getClient().getChecklist().getUrlBase()
              + applicationConfig.getClient().getChecklist().getEndPoint().get(EndPoint.
              HISTORY_FILE.getValue()).getUrl()).uriVariables(pathVariable).toUriString();
      result = exchange(uri, HttpMethod.GET, entity, ChecklistBaseResponse.class);
      log.info("getHistoryFile with checkListMappingId=[{}], result : [{}]",
          checkListMappingId, result);
      return result;
    }
    catch (Exception ex) {
      throwEx(ex, DomainCode.HISTORY_FILE_ERROR, checkListMappingId);
    }
    log.info("getHistoryFile END with checkListMappingId=[{}], result=[{}]",
        checkListMappingId, result);
    return result;
  }

  public ChecklistBaseResponse<FileDto> deleteFile(Long id) {
    log.info("deleteFile START with id=[{}]", id);
    ChecklistBaseResponse<FileDto> result = null;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<?> entity = new HttpEntity<>(headers);
      Map<String, Object> pathVariable = new HashMap<>();
      pathVariable.put("id", id);

      String uri = UriComponentsBuilder
          .fromUriString(applicationConfig.getClient().getChecklist().getUrlBase()
              + applicationConfig.getClient().getChecklist().getEndPoint().get(EndPoint.
              DELETE_FILE.getValue()).getUrl()).uriVariables(pathVariable).toUriString();
      result = exchange(uri, HttpMethod.PUT, entity, ChecklistBaseResponse.class);
      log.info("deleteFile with id=[{}], result : [{}]", id, result);
      return result;
    }
    catch (Exception ex) {
      throwEx(ex, DomainCode.DELETE_FILE_ERROR, id);
    }
    log.info("deleteFile END with id=[{}], result=[{}]",id, result);
    return result;
  }

  public ChecklistBaseResponse<List<PDGroupResponse>> getAllGroup() {
    log.info("getAllGroup START");
    ChecklistBaseResponse<List<PDGroupResponse>> result = null;
    try {
      result = call(EndPoint.GET_GROUP, null, ChecklistBaseResponse.class, HttpMethod.GET);
    }
    catch (Exception ex) {
      throwEx(ex, DomainCode.GET_GROUP_ERROR, null);
    }
    log.info("getAllGroup END with result=[{}]", result);
    return result;
  }

  public SearchResponse search() {
    log.info("search checklist code START: ");
    SearchResponse response = null;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<?> entity = new HttpEntity<>(headers);
      String uri = UriComponentsBuilder.fromUriString(applicationConfig.getClient().getChecklist().getUrlBase()
              + applicationConfig.getClient().getChecklist().getEndPoint().get(EndPoint.SEARCH.getValue()).getUrl())
          .queryParam("keyword", "")
          .queryParam("size", 1000)
          .toUriString();
      response = super.exchange(uri, HttpMethod.GET, entity, SearchResponse.class);
      log.info("checkClient END with request={}, response={}", response);
      return response;
    } catch (Exception ex) {
      throwEx(ex, DomainCode.SEARCH_ERROR, null);
    }
    log.info("search checklist code START END with response=[{}]", response);
    return response;
  }

  private <T> T call(EndPoint endPoint, Object request, Class<T> responseType, HttpMethod method) {
    try {
      String endpoint = this.applicationConfig.getClient().getChecklist().getEndPoint()
          .get(endPoint.getValue()).getUrl();
      log.info("[CALL_API_CHECKLIST] call endPoint:[{}] request:{}", endpoint,
          JsonUtil.convertObject2String(request, objectMapper));

      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<Object> entity = new HttpEntity<>(request, headers);

      String uri = UriComponentsBuilder
          .fromUriString(applicationConfig.getClient().getChecklist()
              .getUrlBase() + endpoint)
          .toUriString();
      T response = exchange(uri, method, entity, responseType);
      log.info("[CALL_API_CHECKLIST] call api [{}] response : {}", endpoint,
          JsonUtil.convertObject2String(response, objectMapper));
      return response;
    } catch (Exception exception) {
      log.error("[CALL_API_CHECKLIST] with endPoint:[{}] , error ",
          endPoint, exception);
      if (Objects.equals(DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR,
          ((ApprovalException)exception).getCode())) {
        throw exception;
      }
      throw new ApprovalException(DomainCode.CHECKLIST_ERROR, new Object[]{request});
    }
  }

  private <T> T callAsBasicAuth(EndPoint endPoint, Object request, Class<T> responseType, HttpMethod method, String basicAuthToken) {
    try {
      String endpoint = this.applicationConfig.getClient().getChecklist().getEndPoint()
          .get(endPoint.getValue()).getUrl();
      log.info("[CALL_API_CHECKLIST] call endPoint:[{}] request:{}", endpoint,
          JsonUtil.convertObject2String(request, objectMapper));

      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("authentication", basicAuthToken);
      HttpEntity<Object> entity = new HttpEntity<>(request, headers);

      String uri = UriComponentsBuilder
          .fromUriString(applicationConfig.getClient().getChecklist()
              .getUrlBase() + endpoint)
          .toUriString();
      T response = exchange(uri, method, entity, responseType);
      log.info("[CALL_API_CHECKLIST] call api [{}] response : [{}]", endpoint,
          JsonUtil.convertObject2String(response, objectMapper));
      return response;
    } catch (Exception exception) {
      log.error("[CALL_API_CHECKLIST] with endPoint:[{}] request:[{}], error ",
          endPoint, JsonUtil.convertObject2String(request, objectMapper), exception);
      if (Objects.equals(DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR,
          ((ApprovalException)exception).getCode())) {
        throw exception;
      }
      throw new ApprovalException(DomainCode.CHECKLIST_ERROR, new Object[]{request});
    }
  }

  private void throwEx(Exception ex, DomainCode typeError, Object request) {
    if (Objects.equals(DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR,
        ((ApprovalException)ex).getCode())) {
      throw new ApprovalException(typeError,((ApprovalException)ex).getArgs());
    }
    throw new ApprovalException(typeError, new Object[]{request});
  }
}
