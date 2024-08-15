package com.msb.bpm.approval.appr.controller.api;

import com.msb.bpm.approval.appr.model.request.checklist.CreateChecklistRequest;
import com.msb.bpm.approval.appr.model.request.checklist.DeleteChecklistGroupRequest;
import com.msb.bpm.approval.appr.model.request.checklist.UpdateAdditionalDataRequest;
import com.msb.bpm.approval.appr.model.request.collateral.ChecklistAssetRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Validated
@RequestMapping("/api/v1/checklist")
public interface ChecklistApi {

  @PostMapping("/upload-file")
  ResponseEntity<ApiResponse> uploadFileChecklist(@Valid @RequestBody CreateChecklistRequest request);

  @PostMapping("/generate/{bpmId}")
  ResponseEntity<ApiResponse> generateChecklist(@PathVariable String bpmId);

  @GetMapping("/reload/{bpmId}")
  ResponseEntity<ApiResponse> reloadChecklist(@PathVariable String bpmId);

  @PutMapping("/additional-data")
  ResponseEntity<ApiResponse> updateAdditionalData(@Valid @RequestBody UpdateAdditionalDataRequest request);

  @PutMapping("/delete-checklist-group")
  ResponseEntity<ApiResponse> deleteChecklistGroup(@Valid @RequestBody DeleteChecklistGroupRequest request);

  @PutMapping("/version/{applicationId}")
  ResponseEntity<ApiResponse> updateChecklistVersion(@PathVariable Long applicationId);

  @GetMapping("/file/history/{checkListMappingId}")
  ResponseEntity<ApiResponse> getHistoryFile(@PathVariable Long checkListMappingId);

  @PutMapping("/file/delete/{id}")
  ResponseEntity<ApiResponse> deleteFile(@PathVariable Long id);

  @GetMapping("/group/get-all")
  ResponseEntity<ApiResponse> getAll();

  @GetMapping("/file/pre-signed-upload/{bpmId}")
  ResponseEntity<ApiResponse> getPreSignedUpload(
      @PathVariable String bpmId,
      @RequestParam(value = "fileName") @NotEmpty String fileName);

  @GetMapping("/file/pre-signed-download/{bpmId}")
  ResponseEntity<ApiResponse> getPreSignedDownload(@PathVariable String bpmId, @RequestParam(value = "filePath") String filePath);

  @PostMapping("/reload/asset")
  ResponseEntity<ApiResponse> generateChecklistAsset(@RequestBody ChecklistAssetRequest checklistAssetRequest);
}