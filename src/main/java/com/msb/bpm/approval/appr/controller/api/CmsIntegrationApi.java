package com.msb.bpm.approval.appr.controller.api;

import com.msb.bpm.approval.appr.model.request.cms.PostCmsCreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsDocumentsRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2CreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2DocumentsRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@RequestMapping("/api")
public interface CmsIntegrationApi {

  @PostMapping("/v1/cms-integration/application")
  ResponseEntity<ApiResponse> postCreateApplication(@Valid @RequestBody PostCmsCreateApplicationRequest request);

  @PostMapping(value = "/v1/cms-integration/application/{bpmApplicationId}/documents")
  ResponseEntity<ApiResponse> postChecklistByBpmApplicationId(@Valid @RequestBody PostCmsDocumentsRequest request,
      @PathVariable String bpmApplicationId);

  @PostMapping("/v2/cms-integration/application")
  ResponseEntity<ApiResponse> createOrUpdateApplication(@Valid @RequestBody PostCmsV2CreateApplicationRequest request);

  @PostMapping(value = "/v2/cms-integration/application/{bpmApplicationId}/documents")
  ResponseEntity<ApiResponse> pushDocuments(@PathVariable String bpmApplicationId, @Valid @RequestBody PostCmsV2DocumentsRequest request);

  @GetMapping(value = "/v1/cms-integration/application/credit/organizations/find-organization")
  ResponseEntity<ApiResponse> findOrganization();
}
