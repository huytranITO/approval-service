package com.msb.bpm.approval.appr.controller.external;

import com.msb.bpm.approval.appr.controller.api.ChecklistApi;
import com.msb.bpm.approval.appr.model.request.checklist.CreateChecklistRequest;
import com.msb.bpm.approval.appr.model.request.checklist.DeleteChecklistGroupRequest;
import com.msb.bpm.approval.appr.model.request.checklist.UpdateAdditionalDataRequest;
import com.msb.bpm.approval.appr.model.request.collateral.ChecklistAssetRequest;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.service.checklist.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChecklistController implements ChecklistApi {

  private final ChecklistService checklistService;
  private final ApiRespFactory apiRespFactory;

  @Override
  public ResponseEntity<ApiResponse> uploadFileChecklist(CreateChecklistRequest request) {
    return apiRespFactory.success(checklistService.uploadFileChecklist(request));
  }

  @Override
  public ResponseEntity<ApiResponse> generateChecklist(String bpmId) {
    return apiRespFactory.success(checklistService.generateChecklistFromFE(bpmId));
  }

  @Override
  public ResponseEntity<ApiResponse> reloadChecklist(String bpmId) {
    return apiRespFactory.success(checklistService.reloadChecklist(bpmId, false));
  }

  @Override
  public ResponseEntity<ApiResponse> updateAdditionalData(UpdateAdditionalDataRequest request) {
    checklistService.updateAdditionalData(request);
    return apiRespFactory.success();
  }

  @Override
  public ResponseEntity<ApiResponse> deleteChecklistGroup(DeleteChecklistGroupRequest request) {
    checklistService.deleteChecklistGroup(request);
    return apiRespFactory.success();
  }

  @Override
  public ResponseEntity<ApiResponse> updateChecklistVersion(@PathVariable Long applicationId) {
    checklistService.updateChecklistVersion(applicationId);
    return apiRespFactory.success();
  }

  @Override
  public ResponseEntity<ApiResponse> getHistoryFile(Long checkListMappingId) {
    return apiRespFactory.success(checklistService.getHistoryFile(checkListMappingId));
  }

  @Override
  public ResponseEntity<ApiResponse> deleteFile(Long id) {
    return apiRespFactory.success(checklistService.deleteFile(id));
  }

  @Override
  public ResponseEntity<ApiResponse> getAll() {
    return apiRespFactory.success(checklistService.getAllGroup());
  }

  @Override
  public ResponseEntity<ApiResponse> getPreSignedUpload(String bpmId, String fileName) {
    return apiRespFactory.success(checklistService.getPreSignedUpload(bpmId, fileName));
  }

  @Override
  public ResponseEntity<ApiResponse> getPreSignedDownload(String bpmId, String filePath) {
    return apiRespFactory.success(checklistService.getPreSignedDownload(bpmId, filePath));
  }

  @Override
  public ResponseEntity<ApiResponse> generateChecklistAsset(ChecklistAssetRequest checklistAssetRequest) {
    return apiRespFactory.success(checklistService.reloadChecklistAsset(checklistAssetRequest));
  }
}
