package com.msb.bpm.approval.appr.controller.api;

import static com.msb.bpm.approval.appr.constant.PermissionConstant.COLLATERAL_OPRISK;
import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_AML_OPR_QUERY;
import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_APPLICATION_ALLOCATE;
import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_APPLICATION_CLOSE;
import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_APPLICATION_ENDORSE;
import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_APPLICATION_INITIALIZE;
import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_APPLICATION_REASSIGN;
import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_APPLICATION_REWORK;
import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_APPLICATION_VIEW;
import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_CIC_QUERY;
import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_RATING_QUERY;
import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_SUBTASK_SAVE;
import static com.msb.bpm.approval.appr.constant.PermissionConstant.APPLICATION_PUSH_KAFKA;

import com.msb.bpm.approval.appr.enums.application.AssignType;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.model.request.IntegrationRequest;
import com.msb.bpm.approval.appr.model.request.IntegrationRetryRequest;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.model.request.cbt.SearchByCustomerRelations;
import com.msb.bpm.approval.appr.model.request.data.PostCreateFeedbackRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostAssignRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostCloseApplicationRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostCreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostReworkApplicationRequest;
import com.msb.bpm.approval.appr.model.request.oprisk.OpRiskRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryApplicationRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCICRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest;
import com.msb.bpm.approval.appr.model.request.query.PostSyncAmlOprRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@RequestMapping("/api/v1/application")
public interface ApplicationApi {

  @PostMapping("/initialize")
  @PreAuthorize(MANAGE_APPLICATION_INITIALIZE)
  ResponseEntity<ApiResponse> postInitializeApplication(
      @Valid @RequestBody PostCreateApplicationRequest request);

  @PreAuthorize(MANAGE_APPLICATION_VIEW)
  @GetMapping("/{bpmId}/detail")
  ResponseEntity<ApiResponse> getApplication(@PathVariable String bpmId);

  @PreAuthorize(MANAGE_SUBTASK_SAVE)
  @PostMapping("/{bpmId}/save-subtask")
  ResponseEntity<ApiResponse> postSaveApplicationData(@PathVariable String bpmId,
      @RequestBody PostBaseRequest request);

  @PreAuthorize(MANAGE_CIC_QUERY)
  @PostMapping("/{bpmId}/cic")
  ResponseEntity<ApiResponse> postQueryCICInfo(@PathVariable String bpmId,
      @Valid @RequestBody PostQueryCICRequest request);

  @PreAuthorize(MANAGE_AML_OPR_QUERY)
  @PostMapping("/{bpmId}/aml-opr")
  ResponseEntity<ApiResponse> postSyncAmlOprInfo(@PathVariable String bpmId,
      @Valid @RequestBody PostSyncAmlOprRequest request);

  @PreAuthorize(MANAGE_RATING_QUERY)
  @PostMapping("/{bpmId}/credit-rating-score")
  ResponseEntity<ApiResponse> postSyncCreditRatingScore(@PathVariable String bpmId,
      @Valid @RequestBody PostQueryCreditRatingRequest request);

  @PreAuthorize(MANAGE_APPLICATION_CLOSE)
  @PostMapping("/{bpmId}/close")
  ResponseEntity<ApiResponse> postCloseApplication(@PathVariable String bpmId,
      @Valid @RequestBody PostCloseApplicationRequest request);

  @PreAuthorize(MANAGE_APPLICATION_REWORK)
  @PostMapping("/{bpmId}/rework")
  ResponseEntity<ApiResponse> postReworkApplication(@PathVariable String bpmId,
      @Valid @RequestBody PostReworkApplicationRequest request);

  @PreAuthorize(MANAGE_APPLICATION_ENDORSE)
  @PostMapping("/{bpmId}/submit")
  ResponseEntity<ApiResponse> postSubmitApplication(@PathVariable String bpmId,
      @Valid @RequestBody PostBaseRequest postBaseRequest);

  @PreAuthorize(MANAGE_APPLICATION_REASSIGN)
  @PostMapping("/handling-officer/assignment")
  ResponseEntity<ApiResponse> postApplicationHandlingOfficer(
      @Valid @RequestBody PostAssignRequest request);

  @PreAuthorize(MANAGE_APPLICATION_ALLOCATE)
  @PostMapping("/coordinator/assignment")
  ResponseEntity<ApiResponse> postApplicationCoordinator(
      @Valid @RequestBody PostAssignRequest request);

  @PreAuthorize(MANAGE_APPLICATION_ALLOCATE)
  @PostMapping("/{bpmId}/tl-coordinator")
  ResponseEntity<ApiResponse> postCoordinatorAssignToTeamLead(@PathVariable String bpmId);

  @GetMapping("/{bpmId}/feedback/history")
  ResponseEntity<ApiResponse> getApplicationFeedbackHistory(@PathVariable String bpmId);

  @PostMapping("/{bpmId}/feedback")
  ResponseEntity<ApiResponse> postCreateFeedback(@PathVariable String bpmId,
      @Valid @RequestBody PostCreateFeedbackRequest request);

  @PutMapping("/{bpmId}/feedback/{id}")
  ResponseEntity<ApiResponse> putUpdateFeedback(@PathVariable String bpmId, @PathVariable Long id,
      @Valid @RequestBody PostCreateFeedbackRequest request);

  @DeleteMapping("/{bpmId}/feedback/{id}")
  ResponseEntity<ApiResponse> deleteFeedback(@PathVariable String bpmId, @PathVariable Long id);

  @PostMapping("/by-customer")
  ResponseEntity<ApiResponse> postQueryApplicationByCustomer(
      @Valid @RequestBody PostQueryApplicationRequest request);

  @GetMapping("/{bpmId}/approval")
  ResponseEntity<ApiResponse> getApplicationApprovalHistory(@PathVariable String bpmId);

  @GetMapping("/{bpmId}/rework/users")
  ResponseEntity<ApiResponse> getReworkUserByApplication(@PathVariable String bpmId);

  @GetMapping("/{bpmId}/authorities")
  ResponseEntity<ApiResponse> getAuthorities(@PathVariable String bpmId);

  @GetMapping("/{bpmId}/{code}/receive/users")
  ResponseEntity<ApiResponse> getReceiveUsers(@PathVariable String bpmId,
      @PathVariable String code);

  @GetMapping("/assign/users")
  ResponseEntity<ApiResponse> getAssignUsers(@RequestParam("type") AssignType assignType,
      @RequestParam(value = "role", required = false)
      ProcessingRole processingRole);

  @GetMapping("/{bpmId}/generate-preview-form")
  ResponseEntity<ApiResponse> getGenerateAndPreviewForms(@PathVariable String bpmId);

  @GetMapping("/integration/historic")
  ResponseEntity<ApiResponse> getHistoricIntegration(@RequestParam int page, @RequestParam int size,
      @RequestParam String sortField, @RequestParam int sortOrder);

  @PostMapping("/integration/search")
  ResponseEntity<ApiResponse> getHistoricIntegrationSearch(@RequestBody IntegrationRequest request);

  @GetMapping("/integration/detail/{bpmId}")
  ResponseEntity<ApiResponse> getDetailHistoricIntegration(@PathVariable String bpmId);

  @PostMapping("/integration/historic/retry")
  ResponseEntity<ApiResponse> integrationHistoricRetry(
      @Valid @RequestBody IntegrationRetryRequest requestList);

  @PutMapping("/{bpmId}/default-generator-status")
  ResponseEntity<ApiResponse> updateDefaultGeneratorStatus(@PathVariable String bpmId);

  @PutMapping("/{bpmId}/push-kafka")
  ResponseEntity<ApiResponse> pushKafka(@PathVariable String bpmId);

  @PostMapping("/{bpmId}/verify-submit-application")
  ResponseEntity<ApiResponse> verifySubmitApplication(@PathVariable String bpmId);

  @GetMapping("/get-account-info")
  ResponseEntity<ApiResponse> getAccountInfo(@RequestParam(required = false) String cifNumber);

  @PreAuthorize(MANAGE_APPLICATION_VIEW)
  @GetMapping("/{bpmId}/copy")
  ResponseEntity<ApiResponse> copyApplication(@PathVariable String bpmId);

  @PreAuthorize(MANAGE_APPLICATION_VIEW)
  @GetMapping("/{bpmId}/cic-data")
  ResponseEntity<ApiResponse> getCicDetail(@PathVariable String bpmId);

  @GetMapping("/get-customer-asset-info-by-bpmid/{bpmId}")
  ResponseEntity<ApiResponse> searchCusIdAndversionListByBpmId(@PathVariable String bpmId);

  @PostMapping("/get-by-relations")
  ResponseEntity<ApiResponse> searchByRelations(@Valid @RequestBody SearchByCustomerRelations request);
  @PreAuthorize(COLLATERAL_OPRISK)
  @PostMapping("/sync-oprisk")
  ResponseEntity<ApiResponse> syncOpRisk(@RequestBody @Valid OpRiskRequest opRiskRequest);

  @GetMapping("/{bpmId}/oprisk-detail")
  ResponseEntity<ApiResponse> getOpRiskDetail(@PathVariable String bpmId);

  @GetMapping("/find-branch")
  ResponseEntity<ApiResponse> findBranch();

  @PreAuthorize(APPLICATION_PUSH_KAFKA)
  @GetMapping("/{bpmId}/get-application-push-kafka")
  ResponseEntity<ApiResponse> getApplicationPushKafka(@PathVariable String bpmId);

  @PreAuthorize(APPLICATION_PUSH_KAFKA)
  @PostMapping("/application-push-kafka/{bpmId}")
  ResponseEntity<ApiResponse> pushKafkaApplication(@PathVariable @Valid @NotBlank String bpmId);


  @PostMapping("/application-push-cic/{bpmId}")
  ResponseEntity<ApiResponse> pushKafkaCIC(@PathVariable @Valid @NotBlank String bpmId,
                                           @RequestBody String cicData);
}