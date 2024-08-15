package com.msb.bpm.approval.appr.client.usermanager;

import static com.msb.bpm.approval.appr.enums.authority.AuthorityStatus.ACTIVE;
import static com.msb.bpm.approval.appr.exception.DomainCode.GET_USER_BY_ROLES_ERROR;

import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.config.card.request.CreateClientRequest;
import com.msb.bpm.approval.appr.config.card.request.CreateSubCardRequest;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.organization.GetOrganizationRequest;
import com.msb.bpm.approval.appr.model.request.user.UserRoleRequest;
import com.msb.bpm.approval.appr.model.response.usermanager.GetOrganizationResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetRoleUserResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.UserManagerPermission;
import com.msb.bpm.approval.appr.model.response.usermanager.UserManagerRegionArea;
import com.msb.bpm.approval.appr.model.response.usermanager.UserManagerRegionArea.DataResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserManagerClient extends AbstractClient {

  private final RestTemplate restTemplate;
  @Autowired
  private ApplicationConfig applicationConfig;

  public Boolean getPermission(String code) {
    log.info("method: getPermission Start ");
    boolean response = false;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<String> entity = new HttpEntity<>(headers);
      String uri = UriComponentsBuilder.fromUriString(applicationConfig.getClient().getUserManager()
              .getUrlBase() + applicationConfig.getClient().getUserManager().getUrlPermission())
          .queryParam("code", code)
          .toUriString();
      UserManagerPermission userManagerPermission = super.exchange(uri, HttpMethod.GET, entity,
          UserManagerPermission.class);
      response = userManagerPermission.getData().isHasPermissions();
      log.info("method: getPermission End {}", response);
    } catch (Exception e) {
      log.error("method: getPermission {}", e.getMessage());
      throw new ApprovalException(DomainCode.GET_PERMISSION_ERROR, new Object[]{code});
    }
    return response;
  }

  @Override
  protected RestTemplate getRestTemplate() {
    return this.restTemplate;
  }

  public DataResponse getRegionAreaByUserName(String userName, Object... param) {
    log.info("getRegionDomainByUserName START with userName={}", userName);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      String token = HeaderUtil.getToken();
      headers.setBearerAuth(token);
      log.info("getRegionAreaByUserName with userName={}, token={}", userName, token);
      HttpEntity<String> entity = new HttpEntity<>(headers);
      String uri = UriComponentsBuilder.fromUriString(applicationConfig.getClient().getUserManager()
              .getUrlBase() + applicationConfig.getClient().getUserManager().getUrlGetRegionArea())
          .queryParam("userName", userName)
          .queryParam("businessType", "APPROVAL")
          .queryParamIfPresent("random", param.length > 0 ? Optional.ofNullable((Boolean)param[0]) : Optional.ofNullable(Boolean.FALSE))
          .toUriString();
      UserManagerRegionArea response = super.exchange(uri, HttpMethod.GET, entity, UserManagerRegionArea.class);
      log.info("getRegionDomainByUserName END with userName={}, response={}", userName, response);
      return response.getData();
    } catch (Exception e) {
      log.error("getRegionDomainByUserName with userName={} -> Error: ", userName, e);
      throw new ApprovalException(DomainCode.GET_REGION_AREA_ERROR, new Object[]{userName});
    }
  }

  /**
   * Lấy danh sách user theo roles & organization code
   *
   * @param roles                 Set<String> Danh sách role
   * @param organizationCode      String      Mã ĐVKD
   * @return                      List<GetRoleUserResponse>
   */
  public GetRoleUserResponse getUserByRoles(Set<String> roles, String organizationCode) {
    log.info("getUserByRoles START with roles {} and organizationCode {}", roles, organizationCode);

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      UserRoleRequest userRoleRequest = new UserRoleRequest();
      userRoleRequest.setRoles(roles);
      userRoleRequest.setStatus(ACTIVE.name()); //lọc user có trạng thái ACTIVE
      if (StringUtils.isNotBlank(organizationCode)) {
        userRoleRequest.setOrganizationCode(organizationCode);
      }
      UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(applicationConfig.getClient().getUserManager()
                      .getUrlBase() + applicationConfig.getClient().getUserManager().getGetUserByRoles());
      HttpEntity<UserRoleRequest> entity = new HttpEntity<>(userRoleRequest, headers);
      GetRoleUserResponse response = super.exchange(uriComponentsBuilder.toUriString(), HttpMethod.POST, entity, GetRoleUserResponse.class);
      log.info("getUserByRoles END with roles {} and organizationCode {} response : {}", roles, organizationCode, response);
      return response;

    } catch (Exception e) {
      log.error("getUserByRoles ERROR with roles {} and organizationCode {} : ", roles, organizationCode, e);
      throw new ApprovalException(GET_USER_BY_ROLES_ERROR);
    }
  }

  /**
   *
   *
   * @param username  String    Username
   * @return GetUserProfileResponse
   */
  public GetUserProfileResponse getUserByUsername(String username) {
    log.info("getUserByUsername START with username {}", username);

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<String> entity = new HttpEntity<>(headers);

      UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(applicationConfig.getClient().getUserManager()
              .getUrlBase() + applicationConfig.getClient().getUserManager().getGetUserByUsername())
          .queryParam("username", username);

      GetUserProfileResponse response = super.exchange(uriComponentsBuilder.toUriString(), HttpMethod.GET, entity, GetUserProfileResponse.class);
      log.info("getUserByUsername END with username {} response : {}", username, response);
      return response;

    } catch (Exception e) {
      log.error("getUserByUsername ERROR with username {} : ", username, e);
      throw e;
    }
  }

  public GetOrganizationResponse findOrganization(GetOrganizationRequest request) {
    log.info("findOrganization START: ");
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      String token = HeaderUtil.getToken();
      headers.setBearerAuth(token);
      HttpEntity<GetOrganizationRequest> entity = new HttpEntity<>(request, headers);
      String uri = UriComponentsBuilder.fromUriString(applicationConfig.getClient().getUserManager()
              .getUrlBase() + applicationConfig.getClient().getUserManager().getFindOrganization())
          .toUriString();
      GetOrganizationResponse response = super.exchange(uri, HttpMethod.POST, entity, GetOrganizationResponse.class);
      log.info("findOrganization END with response={}", response);
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

}
