package com.msb.bpm.approval.appr.service.cms;

import com.msb.bpm.approval.appr.model.request.cms.PostCmsCreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsDocumentsRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2CreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2DocumentsRequest;
import com.msb.bpm.approval.appr.model.response.cms.CmsBaseResponse;

public interface CmsIntegrationService {
  Object postCreateApplication(PostCmsCreateApplicationRequest request);
  Object postChecklistByBpmApplicationId(PostCmsDocumentsRequest request, String bpmApplicationId);

  CmsBaseResponse v2PostCreateApplication(PostCmsV2CreateApplicationRequest request);

  CmsBaseResponse v2PostDocuments(PostCmsV2DocumentsRequest request, String bpmApplicationId);

  Object findOrganization();
}
