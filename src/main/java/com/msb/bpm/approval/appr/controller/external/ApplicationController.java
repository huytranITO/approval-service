package com.msb.bpm.approval.appr.controller.external;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_IDS;
import static com.msb.bpm.approval.appr.exception.DomainCode.SUCCESS;

import com.msb.bpm.approval.appr.aop.annotation.Validation;
import com.msb.bpm.approval.appr.controller.api.ApplicationApi;
import com.msb.bpm.approval.appr.enums.application.AssignType;
import com.msb.bpm.approval.appr.enums.application.GeneratorStatusEnums;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.kafka.BeanProducer;
import com.msb.bpm.approval.appr.kafka.consumer.CicDsKafkaListener;
import com.msb.bpm.approval.appr.kafka.producer.CmsInfoProducerStrategy;
import com.msb.bpm.approval.appr.kafka.producer.CmsV2InfoProducerStrategy;
import com.msb.bpm.approval.appr.kafka.producer.GeneralInfoProducerStrategy;
import com.msb.bpm.approval.appr.model.dto.authority.UserReceptionDTO;
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
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.service.application.ApplicationService;
import com.msb.bpm.approval.appr.service.application.IntegrationHistoricService;
import com.msb.bpm.approval.appr.service.history.ApplicationFeedbackHistoryService;
import com.msb.bpm.approval.appr.service.history.ApplicationHistoryApprovalService;
import com.msb.bpm.approval.appr.service.user.reception.UserReceptionService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApi {

  private final ApplicationService applicationService;
  private final ApiRespFactory apiRespFactory;
  private final ApplicationFeedbackHistoryService feedbackHistoryService;
  private final ApplicationHistoryApprovalService historyApprovalService;
  private final UserReceptionService userReceptionService;
  private final IntegrationHistoricService integrationHistoricService;
  private final GeneralInfoProducerStrategy generalInfoProducerStrategy;
  private final CmsInfoProducerStrategy cmsInfoProducerStrategy;
  private final CmsV2InfoProducerStrategy cmsV2InfoProducerStrategy;
  private final CicDsKafkaListener cicDsKafkaListener;

  /**
   * Khởi tạo hồ sơ
   *
   * @param request PostInitApplicationRequest
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> postInitializeApplication(
      PostCreateApplicationRequest request) {
    return apiRespFactory.success(applicationService.postCreateApplication(request));
  }

  /**
   * Lấy thông tin chi tiết hồ sơ theo mã hồ sơ
   *
   * @param bpmId String
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> getApplication(String bpmId) {
    return apiRespFactory.success(applicationService.getApplication(bpmId));
  }

  /**
   * Lưu tab thông tin hồ sơ Bao gồm cac tab - Thông tin hồ sơ - Thông tin thực địa - Thông tin
   * khoản vay - Danh mục hò sơ
   *
   * @param bpmId   String
   * @param request PostBaseRequest
   * @return ApiResponse
   */
  @Override
  @Validation
  public ResponseEntity<ApiResponse> postSaveApplicationData(String bpmId,
      PostBaseRequest request) {
    return apiRespFactory.success(applicationService.postSaveData(bpmId, request));
  }

  /**
   * Tra cứu thông tin CIC
   *
   * @param bpmId   String
   * @param request PostQueryCICRequest
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> postQueryCICInfo(String bpmId, PostQueryCICRequest request) {
    return apiRespFactory.success(applicationService.queryCICInfo(bpmId, request));
  }

  /**
   * Đồng bộ thông tin AML - OPRISK
   *
   * @param bpmId   String
   * @param request PostSyncAmlOprRequest
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> postSyncAmlOprInfo(String bpmId,
      PostSyncAmlOprRequest request) {
    return apiRespFactory.success(applicationService.syncAmlOprInfo(bpmId, request));
  }

  /**
   * Đóng hồ sơ
   *
   * @param bpmId   String
   * @param request PostCloseApplicationRequest
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> postCloseApplication(String bpmId,
      PostCloseApplicationRequest request) {
    applicationService.postCloseApplication(bpmId, request);
    return apiRespFactory.success(SUCCESS);
  }

  /**
   * Trả hồ sơ
   *
   * @param bpmId   String
   * @param request PostReworkApplicationRequest
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> postReworkApplication(String bpmId,
      PostReworkApplicationRequest request) {
    applicationService.postReworkApplication(bpmId, request);
    return apiRespFactory.success(SUCCESS);
  }

  /**
   * Đệ trình/hoàn thành hồ sơ
   *
   * @param bpmId           String
   * @param postBaseRequest PostSubmitRequest
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> postSubmitApplication(String bpmId,
      PostBaseRequest postBaseRequest) {
    applicationService.postSubmitApplication(bpmId, postBaseRequest);
    return apiRespFactory.success(SUCCESS);
  }

  /**
   * Chuyển hồ sơ tới CBXL88 8
   *
   * @param request PostAssignRequest
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> postApplicationHandlingOfficer(PostAssignRequest request) {
    applicationService.postApplicationHandlingOfficer(request);
    return apiRespFactory.success(SUCCESS);
  }

  /**
   * Chuyển hồ sơ tới CBĐP
   *
   * @param request PostAssignRequest
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> postApplicationCoordinator(PostAssignRequest request) {
    applicationService.postApplicationCoordinator(request);
    return apiRespFactory.success(SUCCESS);
  }

  /**
   * CBĐP chuyển hồ sơ về Team lead
   *
   * @param bpmId String
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> postCoordinatorAssignToTeamLead(String bpmId) {
    applicationService.postCoordinatorAssignToTeamLead(bpmId);
    return apiRespFactory.success(SUCCESS);
  }

  /**
   * Lấy thông tin lịch sử phản hồi
   *
   * @param bpmId String
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> getApplicationFeedbackHistory(String bpmId) {
    return apiRespFactory.success(feedbackHistoryService.getFeedbackHistory(bpmId));
  }

  /**
   * Tạo mới thông tin phản hồi
   *
   * @param bpmId   String
   * @param request PostCreateFeedbackRequest
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> postCreateFeedback(String bpmId,
      PostCreateFeedbackRequest request) {
    feedbackHistoryService.postCreateFeedback(bpmId, request);
    return apiRespFactory.success(SUCCESS);
  }

  /**
   * Cập nhật thông tin phản hồi
   *
   * @param bpmId   String
   * @param id      Long
   * @param request PostCreateFeedbackRequest
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> putUpdateFeedback(String bpmId, Long id,
      PostCreateFeedbackRequest request) {
    feedbackHistoryService.putUpdateFeedback(bpmId, id, request);
    return apiRespFactory.success(SUCCESS);
  }

  /**
   * Delete feedback
   *
   * @param bpmId
   * @param id
   * @return
   */
  @Override
  public ResponseEntity<ApiResponse> deleteFeedback(String bpmId, Long id) {
    feedbackHistoryService.deleteHistoryFeedback(bpmId, id);
    return apiRespFactory.success(SUCCESS);
  }

  /**
   * Lấy thông tin lịch sử trình hồ sơ của khách hàng
   *
   * @param request PostQueryApplicationRequest
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> postQueryApplicationByCustomer(
      PostQueryApplicationRequest request) {
    return apiRespFactory.success(applicationService.postQueryApplicationByCustomer(request));
  }

  /**
   * Lấy thông tin lịch sử yêu cầu của hồ sơ
   *
   * @param bpmId String
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> getApplicationApprovalHistory(String bpmId) {
    return apiRespFactory.success(historyApprovalService.getApplicationApprovalHistory(bpmId));
  }

  /**
   * Lấy danh sách user tiếp nhận hồ sơ bị trả về
   *
   * @param bpmId String
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> getReworkUserByApplication(String bpmId) {
    return apiRespFactory.success(userReceptionService.getReworkUserByApplication(bpmId));
  }

  /**
   * Lấy danh sách cấp thẩm quyền tiêp nhận hồ sơ
   *
   * @param bpmId String        Mã hồ sơ
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> getAuthorities(String bpmId) {
    return apiRespFactory.success(applicationService.getAuthoritiesByApplication(bpmId));
  }

  /**
   * Lấy danh sách user tiếp nhận hồ sơ được đề xuất
   *
   * @param bpmId String        Mã hồ sơ
   * @param code  String        Mã thẩm quyền
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> getReceiveUsers(String bpmId, String code) {
    return apiRespFactory.success(userReceptionService.getUserByAuthority(bpmId, code));
  }

  /**
   * Lấy danh sách user CBĐP hoặc CBXL để gán hồ sơ
   *
   * @param assignType AssignType
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> getAssignUsers(AssignType assignType,
      ProcessingRole processingRole) {
    List<UserReceptionDTO> users = processingRole != null
        ? userReceptionService.getAssignUsersByRole(processingRole)
        : userReceptionService.getAssignUsers(assignType);
    return apiRespFactory.success(users);
  }

  /**
   * Generate form biểu mẫu/tờ trình
   *
   * @param bpmId String
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> getGenerateAndPreviewForms(String bpmId) {
    return apiRespFactory.success(applicationService.getGenerateAndPreviewForms(bpmId));
  }

  /**
   * Đồng bộ thông Xếp hạng tín dụng từ CSS
   *
   * @param bpmId   String
   * @param request PostQueryCreditRatingRequest
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> postSyncCreditRatingScore(@PathVariable String bpmId,
      @Valid @RequestBody PostQueryCreditRatingRequest request) {
    return apiRespFactory.success(applicationService.syncCreditRatingCSS(bpmId, request));
  }

  /**
   * Lấy danh sách tích hợp lỗi
   *
   * @return
   */
  @Override
  public ResponseEntity<ApiResponse> getHistoricIntegration(@PathVariable int page,
      @PathVariable int size,
      @PathVariable String sortField, @PathVariable int sortOrder) {
    return apiRespFactory.success(
        integrationHistoricService.getHistoricIntegration(page, size, sortField, sortOrder));
  }

  /**
   * Tìm kiến lỗi tích hợp
   *
   * @return
   */
  @Override
  public ResponseEntity<ApiResponse> getHistoricIntegrationSearch(@RequestBody IntegrationRequest request) {
    return apiRespFactory.success(
        integrationHistoricService.getHistoricIntegrationSearch(request .getContents(), request.getPage(),
            request.getSize(), request.getSortField(), request.getSortOrder()));
  }


  /**
   * Xem chi tiết lỗi tích hợp
   *
   * @return
   */
  @Override
  public ResponseEntity<ApiResponse> getDetailHistoricIntegration(@PathVariable String bpmId) {
    return apiRespFactory.success(integrationHistoricService.getHistoricIntegrationDetail(bpmId));
  }

  /**
   * yêu cầu tích hợp lại
   *
   * @return
   */
  @Override
  public ResponseEntity<ApiResponse> integrationHistoricRetry(IntegrationRetryRequest requestList) {
    return apiRespFactory.success(integrationHistoricService.integrationHistoricRetry(requestList));
  }

  /**
   * Cập nhật trạng thái generate form về default = 00
   *
   * @param bpmId String
   * @return ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> updateDefaultGeneratorStatus(String bpmId) {
    applicationService.updateGeneratorStatus(bpmId, GeneratorStatusEnums.DEFAULT.getValue());
    return apiRespFactory.success();
  }

  /**
   * Đẩy lại dữ liệu trạng thái hồ sơ lên Kafka
   *
   * @param bpmId   String
   * @return        ApiResponse
   */
  @Override
  public ResponseEntity<ApiResponse> pushKafka(String bpmId) {
    List<String> bpmIds = Collections.singletonList(bpmId);

    ConcurrentMap<String, Object> metadata = new ConcurrentHashMap<>();
    metadata.put(APPLICATION_BPM_IDS, bpmIds);
    new BeanProducer(metadata, generalInfoProducerStrategy).execute();

    ConcurrentMap<String, Object> broadcastMetadata = new ConcurrentHashMap<>();
    broadcastMetadata.put(APPLICATION_BPM_IDS, bpmIds);
    new BeanProducer(broadcastMetadata, cmsInfoProducerStrategy).execute();

    ConcurrentMap<String, Object> broadcastMetadataV2 = new ConcurrentHashMap<>();
    broadcastMetadataV2.put(APPLICATION_BPM_IDS, bpmIds);
    new BeanProducer(broadcastMetadataV2, cmsV2InfoProducerStrategy).execute();

    return apiRespFactory.success();
  }

  @Override
  public ResponseEntity<ApiResponse> verifySubmitApplication(String bpmId) {
    return apiRespFactory.success(applicationService.verifySubmitApplication(bpmId));
  }

  @Override
  public ResponseEntity<ApiResponse> getAccountInfo(String cifNumber) {
    return apiRespFactory.success(applicationService.getAccountInfo(cifNumber));
  }

  @Override
  public ResponseEntity<ApiResponse> copyApplication(String bpmId) {
    return apiRespFactory.success(applicationService.copyApplication(bpmId));
  }

  @Override
  public ResponseEntity<ApiResponse> getCicDetail(String bpmId) {
    return apiRespFactory.success(applicationService.getCicDetail(bpmId));
  }

  @Override
  public ResponseEntity<ApiResponse> searchCusIdAndversionListByBpmId(String bpmId) {
    return apiRespFactory.success(applicationService.searchIdAndVersionList(bpmId));
  }

  @Override
  public ResponseEntity<ApiResponse> searchByRelations(@Valid @RequestBody SearchByCustomerRelations request) {
    return apiRespFactory.success(applicationService.searchByRelations(request));
  }

  @Override
  public ResponseEntity<ApiResponse> syncOpRisk(OpRiskRequest opRiskRequest) {
    return apiRespFactory.success(applicationService.syncOpRisk(opRiskRequest));
  }

  @Override
  public ResponseEntity<ApiResponse> getOpRiskDetail(String bpmId) {
    return apiRespFactory.success(applicationService.getOpRiskDetail(bpmId));
  }

  @Override
  public ResponseEntity<ApiResponse> findBranch() {
    return apiRespFactory.success(applicationService.findBranch());
  }

  @Override
  public ResponseEntity<ApiResponse> getApplicationPushKafka(String bpmId) {
    return apiRespFactory.success(applicationService.getApplicationPushKafka(bpmId));
  }

  @Override
  public ResponseEntity<ApiResponse> pushKafkaApplication(String bpmId) {
    return applicationService.pushKafkaApplication(bpmId);
  }

  @Override
  public ResponseEntity<ApiResponse> pushKafkaCIC(String bpmId, String cícData) {
    cicDsKafkaListener.listenDataCIC(cícData);
    return apiRespFactory.success();
  }
}
