package com.msb.bpm.approval.appr.controller.external;

import com.msb.bpm.approval.appr.controller.api.CmsIntegrationApi;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsCreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsDocumentsRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2CreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2DocumentsRequest;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.service.cms.CmsIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CmsIntegrationController implements CmsIntegrationApi {

  private final ApiRespFactory apiRespFactory;
  private final CmsIntegrationService cmsIntegrationService;

  @Override
  public ResponseEntity<ApiResponse> postCreateApplication(PostCmsCreateApplicationRequest request) {
    return apiRespFactory.success(cmsIntegrationService.postCreateApplication(request));
  }

  @Override
  public ResponseEntity<ApiResponse> postChecklistByBpmApplicationId(PostCmsDocumentsRequest request,
      String bpmApplicationId) {
    return apiRespFactory.success(cmsIntegrationService.postChecklistByBpmApplicationId(request,
        bpmApplicationId));
  }

  @Override
  public ResponseEntity<ApiResponse> createOrUpdateApplication(PostCmsV2CreateApplicationRequest request) {
    return apiRespFactory.success(cmsIntegrationService.v2PostCreateApplication(request));
  }

  @Override
  public ResponseEntity<ApiResponse> pushDocuments(String bpmApplicationId, PostCmsV2DocumentsRequest request) {
    return apiRespFactory.success(cmsIntegrationService.v2PostDocuments(request, bpmApplicationId));
  }

  @Override
  public ResponseEntity<ApiResponse> findOrganization() {
    return apiRespFactory.success(cmsIntegrationService.findOrganization());
  }

}
