package com.msb.bpm.approval.appr.service.checklist.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.INDIVIDUAL_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.FINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.CUSTOMER_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DOCUMENTS_INFO;
import static com.msb.bpm.approval.appr.constant.Constant.YYYY_MM_DD_FORMAT;
import static com.msb.bpm.approval.appr.enums.checklist.Group.APPROVAL_PROFILE;
import static com.msb.bpm.approval.appr.enums.checklist.Group.INCOME_FROM_DN_HGD_HKD;
import static com.msb.bpm.approval.appr.enums.checklist.Group.INCOME_FROM_ENTERPRISE;
import static com.msb.bpm.approval.appr.enums.checklist.Group.INCOME_FROM_PROPERTY_BUSINESS;
import static com.msb.bpm.approval.appr.enums.checklist.Group.INCOME_FROM_RENTAL_ASSET;
import static com.msb.bpm.approval.appr.enums.checklist.Group.INCOME_FROM_SALARY;
import static com.msb.bpm.approval.appr.enums.checklist.Group.INCOME_OTHER;
import static com.msb.bpm.approval.appr.enums.checklist.Group.LOAN_PROFILE;
import static com.msb.bpm.approval.appr.enums.checklist.Group.OTHER_PROFILE;
import static com.msb.bpm.approval.appr.util.Util.FILE_PATH_MINIO_FORMAT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.checklist.ChecklistClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.config.properties.MinioProperties;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer;
import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.application.SubmissionPurpose;
import com.msb.bpm.approval.appr.enums.checklist.AssetGroup;
import com.msb.bpm.approval.appr.enums.checklist.BusinessFlow;
import com.msb.bpm.approval.appr.enums.checklist.CustomerRelationShip;
import com.msb.bpm.approval.appr.enums.checklist.DomainType;
import com.msb.bpm.approval.appr.enums.checklist.Group;
import com.msb.bpm.approval.appr.enums.common.PhaseCode;
import com.msb.bpm.approval.appr.enums.formtemplate.SegmentTypeEum;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupDto;
import com.msb.bpm.approval.appr.model.entity.ApplicationChecklistEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.CicEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.entity.RuleVersionMappingEntity;
import com.msb.bpm.approval.appr.model.request.checklist.CreateChecklistRequest;
import com.msb.bpm.approval.appr.model.request.checklist.DeleteChecklistGroupRequest;
import com.msb.bpm.approval.appr.model.request.checklist.GetChecklistRequest;
import com.msb.bpm.approval.appr.model.request.checklist.RuleRequest;
import com.msb.bpm.approval.appr.model.request.checklist.UpdateAdditionalDataRequest;
import com.msb.bpm.approval.appr.model.request.collateral.ChecklistAssetRequest;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.checklist.PreSignedMinIOResponse;
import com.msb.bpm.approval.appr.model.response.collateral.AssetRuleChecklistResponse;
import com.msb.bpm.approval.appr.repository.ApplicationChecklistRepository;
import com.msb.bpm.approval.appr.repository.ApplicationHistoryApprovalRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.repository.RuleVersionMappingRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.checklist.ChecklistService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import com.msb.bpm.approval.appr.util.Util;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.http.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChecklistServiceImpl extends AbstractBaseService implements ChecklistService {

  private final ApplicationHistoryApprovalRepository applicationHistoryApprovalRepository;

  private final ChecklistClient checklistClient;
  private final ApplicationChecklistRepository applicationChecklistRepository;
  private final MinioProperties minioProperties;
  private final MinioClient minioClient;
  private final CommonService commonService;
  private final RuleVersionMappingRepository ruleVersionMappingRepository;
  private final ObjectMapper objectMapper;
  private final CustomerRepository customerRepository;
  private final ApplicationRepository applicationRepository;
  private final CollateralClient collateralClient;

  @Override
  public Object uploadFileChecklist(CreateChecklistRequest request) {
    return checklistClient.uploadFileChecklist(request);
  }

  @Override
  public Object uploadFileChecklistInternal(CreateChecklistRequest request, String basicAuthToken) {
    log.info("Saving checklist with token: [{}]", basicAuthToken);
    return checklistClient.uploadFileChecklistInternal(request, basicAuthToken);
  }

  @Override
  public Object generateChecklist(GetChecklistRequest request) {
    return checklistClient.reload(request);
  }

  @Override
  @Transactional
  public Object generateChecklistFromFE(String bpmId) {
    log.info("generateChecklistFromFE START with bpmId={}", bpmId);
    ApplicationEntity entityApp = commonService.findAppByBpmId(bpmId);
    return generateChecklist(entityApp);
  }

  @Override
  @Transactional
  public Object reloadChecklistAsset(ChecklistAssetRequest checklistAssetRequest) {
    log.info("reloadChecklistAsset START with bpmId={}", checklistAssetRequest.getBpmId());
    ApplicationEntity entityApp = commonService.findAppByBpmId(checklistAssetRequest.getBpmId());
    GetChecklistRequest request = buildChecklistRequest(entityApp,
        checklistAssetRequest.getAssetInfoRequests());
    request.setRuleCode(RuleCode.R001.name());
    getDecisionRule(entityApp.getId(), RuleCode.R001, request);
    ChecklistBaseResponse<GroupChecklistDto> response = checklistClient.reload(request);

    // Set trạng thái tab hồ sơ cho danh mục hồ sơ checklist
    Map<String, ApplicationDraftEntity> draftEntityMap = draftMapByTabCode(
        checklistAssetRequest.getBpmId());
    ApplicationDraftEntity applicationDraftEntity = draftEntityMap.get(DOCUMENTS_INFO);
    if (applicationDraftEntity != null) {
      response.getData().setCompleted(Objects.equals(FINISHED, applicationDraftEntity.getStatus()));
    }

    log.info("reloadChecklistAsset END with bpmId={}", checklistAssetRequest.getBpmId());
    saveDBRuleVersion(entityApp, RuleCode.R001, response);
    return response;
  }

  @Override
  @Transactional
  public Object reloadChecklist(String bpmId,  Object... params) {
    log.info("ChecklistServiceImpl.reloadChecklist start with bpmId={}", bpmId);
    ChecklistBaseResponse<GroupChecklistDto> response = null;
    ApplicationEntity entityApp = commonService.findAppByBpmId(bpmId);

    Boolean enable = (Boolean) params[0];
    if (Boolean.FALSE.equals(enable)) {
      // Chỉ khi hồ sơ đang xử lý bởi Assignee thì mới dùng API reload checklist ( vì có thể thay đổi thông tin hồ sơ, dẫn tới thay đổi checklist). Đối với các hồ sơ ở trạng thái view, sử dụng API getbyrequestcode.
      if (Objects.isNull(entityApp.getAssignee())
          || !entityApp.getAssignee().equalsIgnoreCase(SecurityContextUtil.getCurrentUser())
          || ApplicationStatus.AS0099.getValue().equalsIgnoreCase(entityApp.getStatus())
          || ApplicationStatus.AS9999.getValue().equalsIgnoreCase(entityApp.getStatus())) {
        response = getChecklistByRequestCode(entityApp.getBpmId());
        if (Objects.nonNull(response)) return response;
      }
    }

    GetChecklistRequest request = buildChecklistRequest(entityApp, null);
    request.setRuleCode(RuleCode.R001.name());
    getDecisionRule(entityApp.getId(), RuleCode.R001, request);
    response = checklistClient.reload(request);
    highlightChecklistFile(entityApp, response);

    // Set trạng thái tab hồ sơ cho danh mục hồ sơ checklist
    Map<String, ApplicationDraftEntity> draftEntityMap = draftMapByTabCode(bpmId);
    ApplicationDraftEntity applicationDraftEntity = draftEntityMap.get(DOCUMENTS_INFO);
    if (applicationDraftEntity != null) {
      response.getData().setCompleted(Objects.equals(FINISHED, applicationDraftEntity.getStatus()));
    }
    response.getData().setRmCommitStatus(entityApp.isRmCommitStatus());

    saveDBRuleVersion(entityApp, RuleCode.R001, response);
    return response;
  }

  private void highlightChecklistFile(ApplicationEntity application,
      ChecklistBaseResponse<GroupChecklistDto> response) {
    if (application.getStatus() != null
        && ApplicationStatus.isComplete(application.getStatus())) {
      return;
    }
    ProcessingRole processingRole = ProcessingRole.valueOf(application.getProcessingRole());
    log.info("[HIGHLIGHT_CHECKLIST] processingRole : {}", processingRole);
    ApplicationHistoryApprovalEntity lastTimeAssignToOtherRole
        = applicationHistoryApprovalRepository.findFirstByApplicationAndFromUserRoleOrderByExecutedAtDesc(
        application,
        processingRole);
    ApplicationHistoryApprovalEntity lastTimeReceiverFromOtherRole
        = applicationHistoryApprovalRepository.findFirstByApplicationAndUserRoleOrderByExecutedAtDesc(
        application,
        processingRole);
    if (lastTimeAssignToOtherRole == null
        || lastTimeAssignToOtherRole.getExecutedAt() == null) {
      return;
    }
    if (lastTimeReceiverFromOtherRole != null) {
      log.info("[HIGHLIGHT_CHECKLIST] lastTimeReceiverFromOtherRole : {}",
          lastTimeReceiverFromOtherRole.toString());
      log.info("[HIGHLIGHT_CHECKLIST] lastTimeReceiverFromOtherRole : {}",
          lastTimeReceiverFromOtherRole.getExecutedAt());

    }
    if (lastTimeAssignToOtherRole != null) {
      log.info("[HIGHLIGHT_CHECKLIST] lastTimeAssignToOtherRole : {}",
          lastTimeAssignToOtherRole.toString());
      log.info("[HIGHLIGHT_CHECKLIST] lastTimeAssignToOtherRole : {}",
          lastTimeAssignToOtherRole.getExecutedAt());
    }
    response.getData().getListChecklist().stream()
        .filter(checklistDto -> checklistDto != null && checklistDto.getListFile() != null)
        .forEach(checklistDto ->
            checklistDto.getListFile().stream()
                .filter(fileDto -> fileDto.getCreatedAt() != null)
                .forEach(fileDto -> {
                  if (fileDto.getCreatedAt().isBefore(lastTimeReceiverFromOtherRole.getExecutedAt())
                      && fileDto.getCreatedAt()
                      .isAfter(lastTimeAssignToOtherRole.getExecutedAt())) {
                    fileDto.setFileOfPreviousRoles(true);
                  }
                }));
  }

  @Override
  @Transactional
  public Object reloadChecklistCIC(String bpmId, List<CicEntity> cicEntities) {
    log.info("reloadChecklistCIC START with bpmId={}", bpmId);
    ApplicationEntity entityApp = commonService.findAppByBpmId(bpmId);
    GetChecklistRequest request = buildChecklistRequestCIC(entityApp, cicEntities);
    request.setRuleCode(RuleCode.R001.name());
    getDecisionRule(entityApp.getId(), RuleCode.R001, request);
    ChecklistBaseResponse<GroupChecklistDto> response = checklistClient.reload(request);

    // Set trạng thái tab hồ sơ cho danh mục hồ sơ checklist
    Map<String, ApplicationDraftEntity> draftEntityMap = draftMapByTabCode(bpmId);
    ApplicationDraftEntity applicationDraftEntity = draftEntityMap.get(DOCUMENTS_INFO);
    if (applicationDraftEntity != null) {
      response.getData().setCompleted(Objects.equals(FINISHED, applicationDraftEntity.getStatus()));
    }

    log.info("reloadChecklistCIC END with bpmId={}", bpmId);
    saveDBRuleVersion(entityApp, RuleCode.R001, response);
    return response;
  }

  private void saveDBRuleVersion(
      ApplicationEntity entityApp,
      RuleCode ruleCode,
      ChecklistBaseResponse<GroupChecklistDto> response) {
    List<RuleVersionMappingEntity> ruleVersionMapping =
        ruleVersionMappingRepository
            .findByApplicationIdAndRuleCode(entityApp.getId(), ruleCode)
            .orElse(null);
    List<RuleVersionMappingEntity> lstRuleEntity = new ArrayList<>();
    for (GroupDto group : response.getData().getListGroup()) {
      if (CollectionUtils.isEmpty(ruleVersionMapping)
          || (CollectionUtils.isNotEmpty(ruleVersionMapping)
          && Objects.nonNull(group.getCode())
          && Objects.nonNull(Group.get(group.getCode()))
          && ruleVersionMapping.stream()
          .noneMatch(e -> group.getCode().equals(e.getGroupCode())))) {
        lstRuleEntity.add(
            new RuleVersionMappingEntity()
                .withRuleVersion(
                    Objects.nonNull(group.getRuleVersion())
                        ? group.getRuleVersion().intValue()
                        : null)
                .withApplication(entityApp)
                .withRuleCode(ruleCode)
                .withGroupCode(group.getCode()));
      }
    }
    if (CollectionUtils.isNotEmpty(lstRuleEntity)) {
      ruleVersionMappingRepository.saveAllAndFlush(lstRuleEntity);
    }
  }

  @Override
  public void updateAdditionalData(UpdateAdditionalDataRequest request) {
    checklistClient.updateAdditionalData(request);
  }

  @Override
  public void deleteChecklistGroup(DeleteChecklistGroupRequest request) {
    checklistClient.deleteChecklistGroup(request);
  }

  @Override
  public void updateChecklistVersion(Long applicationId) {
    checklistClient.updateChecklistVersion(applicationId);
  }

  @Override
  public Object getHistoryFile(Long checkListMappingId) {
    return checklistClient.getHistoryFile(checkListMappingId);
  }

  @Override
  public Object deleteFile(Long id) {
    return checklistClient.deleteFile(id);
  }

  @Override
  public Object getAllGroup() {
    return checklistClient.getAllGroup();
  }

  @Override
  @Transactional
  public Object generateChecklist(ApplicationEntity entityApp) {
    log.info("generateChecklist START with bpmId={}", entityApp.getBpmId());
    try {
      GetChecklistRequest request = buildChecklistRequest(entityApp, null);
      request.setRuleCode(RuleCode.R001.name());
      getDecisionRule(entityApp.getId(), RuleCode.R001, request);
      ChecklistBaseResponse<GroupChecklistDto> response =
          (ChecklistBaseResponse<GroupChecklistDto>) generateChecklist(request);
      Map<String, Long> checklistMappingIds = new HashMap<>();
      if (Objects.nonNull(response.getData())
          && CollectionUtils.isNotEmpty(response.getData().getListChecklist())) {
        response.getData().getListChecklist().stream()
            .forEach(e -> checklistMappingIds.put(e.getGroupCode(), e.getChecklistMappingId()));
      }
      saveDBChecklist(
          entityApp.getId(), JsonUtil.convertObject2String(checklistMappingIds, objectMapper));
      saveDBRuleVersion(entityApp, RuleCode.R001, response);
      log.info("generateChecklist END with bpmId={}", entityApp.getBpmId());
      return response;

    } catch (Exception ex) {
      log.error("generateChecklist with bpmId={}, Error: ", entityApp.getBpmId(), ex);
    }
    return null;
  }

  @Override
  public PreSignedMinIOResponse getPreSignedUpload(String bpmId, String fileName) {
    log.info("getPreSignedUpload START with fileName={}", fileName);
    Optional<ApplicationEntity> applicationEntityOps = applicationRepository.findByBpmId(bpmId);
    if (!applicationEntityOps.isPresent()) {
      throw new ApprovalException(HttpStatus.BAD_REQUEST, DomainCode.INVALID_PARAMETER);
    }
    try {
      String filePath = genFilePathMinIO(bpmId, fileName);
      GetPresignedObjectUrlArgs preSigned =
          GetPresignedObjectUrlArgs.builder()
              .bucket(minioProperties.getMinio().getBucket())
              .object(filePath)
              .expiry(minioProperties.getMinio().getExpiry(), TimeUnit.MINUTES)
              .method(Method.PUT)
              .build();

      return PreSignedMinIOResponse.builder()
          .url(minioClient.getPresignedObjectUrl(preSigned))
          .filePath(filePath)
          .bucket(minioProperties.getMinio().getBucket())
          .build();
    } catch (Exception ex) {
      log.error("getPreSignedUpload END with fileName={}, Error:", fileName, ex);
      throw new ApprovalException(DomainCode.GET_PRE_SIGNED_ERROR, new Object[]{fileName});
    }
  }

  private String genFilePathMinIO(String bpmId, String fileName) {
    return String.format(
        FILE_PATH_MINIO_FORMAT,
        minioProperties.getMinio().getFilePathBase(),
        Util.getCurrDate(YYYY_MM_DD_FORMAT),
        bpmId,
        fileName);
  }

  @Override
  public PreSignedMinIOResponse getPreSignedDownload(String bpmId, String filePath) {
    log.info("getPreSignedDownload START with filePath={}", filePath);
    Optional<ApplicationEntity> applicationEntityOps = applicationRepository.findByBpmId(bpmId);
    if (!applicationEntityOps.isPresent()) {
      throw new ApprovalException(HttpStatus.BAD_REQUEST, DomainCode.INVALID_PARAMETER);
    }
    try {
      GetPresignedObjectUrlArgs preSigned =
          GetPresignedObjectUrlArgs.builder()
              .bucket(minioProperties.getMinio().getBucket())
              .object(filePath)
              .expiry(minioProperties.getMinio().getExpiry(), TimeUnit.MINUTES)
              .method(Method.GET)
              .build();

      return PreSignedMinIOResponse.builder()
          .url(minioClient.getPresignedObjectUrl(preSigned))
          .filePath(filePath)
          .build();
    } catch (Exception ex) {
      log.error("getPreSignedDownload END with filePath={}, Error:", filePath, ex);
      throw new ApprovalException(DomainCode.GET_PRE_SIGNED_ERROR, new Object[]{filePath});
    }
  }

  @Override
  public Long getSizeByFilePathMinIO(String filePath) {
    log.info("getSizeByFilePathMinIO START with filePath={}", filePath);
    try {
      StatObjectArgs statObjectArgs =
          StatObjectArgs.builder()
              .bucket(minioProperties.getMinio().getBucket())
              .object(filePath)
              .build();
      StatObjectResponse statObject = minioClient.statObject(statObjectArgs);
      log.info("getSizeByFilePathMinIO END with filePath={}, size={}", filePath, statObject.size());
      return statObject.size();
    } catch (Exception ex) {
      log.error("getSizeByFilePathMinIO END with filePath={}, Error:", filePath, ex);
      throw new ApprovalException(DomainCode.GET_SIZE_FILE_MINIO_ERROR, new Object[]{filePath});
    }
  }

  @Override
  public ChecklistBaseResponse<GroupChecklistDto> getChecklistByRequestCode(String requestCode) {
    return checklistClient.getChecklistByRequestCode(requestCode);
  }

  public GetChecklistRequest buildChecklistRequestCIC(ApplicationEntity entityApp,
      List<CicEntity> cicEntities) {
    GetChecklistRequest request = buildChecklistRequest(entityApp, null);
    request.setRuleCode(RuleCode.R001.name());

    List<RuleRequest> listRuleRequest = request.getListRuleRequest();
    Map<String, List<RuleRequest>> ruleRequestMap = listRuleRequest.stream()
        .collect(Collectors.groupingBy(
            o -> o.getGroupCode() + o.getDomainType() + o.getDomainObjectId()));

    for (CicEntity cicEntity : cicEntities) {
      String groupCode = getGroupCode(cicEntity.getRelationship());
      Long customerId = cicEntity.getRefCustomerId();
      List<RuleRequest> ruleRequests = ruleRequestMap.get(
          groupCode + DomainType.CUSTOMER + customerId);
      if (ruleRequests == null) {
        RuleRequest ruleRequest = buildRuleRequest(cicEntity);
        listRuleRequest.add(ruleRequest);
      }
    }

    return request;
  }

  private String getGroupCode(String relationshipType) {
    if (!org.springframework.util.StringUtils.hasText(relationshipType)) {
      return Group.DEBT_GUARANTOR_LEGAL_PROFILE.getValue();
    }
    if (CustomerRelationShip.isCustomer(relationshipType)) {
      return Group.CUSTOMER_LEGAL_PROFILE.getValue();
    } else {
      return CustomerRelationShip.isSpouse(relationshipType) ?
          Group.SPOUSE_LEGAL_PROFILE.getValue() : Group.DEBT_GUARANTOR_LEGAL_PROFILE.getValue();
    }
  }

  private RuleRequest buildRuleRequest(CicEntity cicEntity) {
    if (!org.springframework.util.StringUtils.hasText(cicEntity.getRelationship())) {
      return RuleRequest.builder()
          .rootGroup(Group.LEGAL_PROFILE.getValue())
          .domainType(CUSTOMER_TAG)
          .domainObjectId(cicEntity.getRefCustomerId())
          .customerType(Customer.RB)
          .customerRelationShip(cicEntity.getRelationship())
          .groupCode(Group.DEBT_GUARANTOR_LEGAL_PROFILE.getValue())
          .build();
    }
    if (CustomerRelationShip.isCustomer(cicEntity.getRelationship())) {
      return RuleRequest.builder()
          .rootGroup(Group.LEGAL_PROFILE.getValue())
          .domainType(CUSTOMER_TAG)
          .domainObjectId(cicEntity.getRefCustomerId())
          .customerType(Customer.RB)
          .customerRelationShip(CustomerRelationShip.CUSTOMER)
          .groupCode(Group.CUSTOMER_LEGAL_PROFILE.getValue())
          .build();
    }

    if (CustomerRelationShip.isSpouse(cicEntity.getRelationship())) {
      return RuleRequest.builder()
          .rootGroup(Group.LEGAL_PROFILE.getValue())
          .domainType(CUSTOMER_TAG)
          .domainObjectId(cicEntity.getRefCustomerId())
          .customerType(Customer.RB)
          .customerRelationShip(cicEntity.getRelationship())
          .groupCode(Group.SPOUSE_LEGAL_PROFILE.getValue())
          .build();

    }

    return RuleRequest.builder()
        .rootGroup(Group.LEGAL_PROFILE.getValue())
        .domainType(CUSTOMER_TAG)
        .domainObjectId(cicEntity.getRefCustomerId())
        .customerType(Customer.RB)
        .customerRelationShip(cicEntity.getRelationship())
        .groupCode(Group.DEBT_GUARANTOR_LEGAL_PROFILE.getValue())
        .build();
  }

  public GetChecklistRequest buildChecklistRequest(ApplicationEntity entityApp,
      List<AssetRuleChecklistResponse> lstAssetRuleChecklist) {
    List<RuleRequest> lstRuleRequests = new ArrayList<>();
    // Hồ sơ pháp lý 01 : LEGAL_PROFILE
    // KH
    RuleRequest ruleRequestKH =
        RuleRequest.builder()
            .rootGroup(Group.LEGAL_PROFILE.getValue())
            .domainType(CUSTOMER_TAG)
            .domainObjectId(entityApp.getCustomer().getRefCustomerId())
            .customerType(Customer.RB)
            .customerRelationShip(CustomerRelationShip.CUSTOMER)
            .groupCode(Group.CUSTOMER_LEGAL_PROFILE.getValue())
            .build();

    lstRuleRequests.add(ruleRequestKH);
    // NHP & OTH
    List<RuleRequest> lstRuleNHP = new ArrayList<>();
    List<RuleRequest> lstRuleOTH = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(entityApp.getCustomer().getCustomerRelationShips())) {
      for (CustomerRelationShipEntity item : entityApp.getCustomer().getCustomerRelationShips()) {
        if (StringUtils.isBlank(item.getRelationship())) {
          continue;
        }

        CustomerEntity customerEntity =
            customerRepository.getReferenceById(item.getCustomerRefId());

        if (CustomerRelationShip.isSpouse(item.getRelationship())) {
          lstRuleNHP.add(
              RuleRequest.builder()
                  .rootGroup(Group.LEGAL_PROFILE.getValue())
                  .domainType(CUSTOMER_TAG)
                  .domainObjectId(customerEntity.getRefCustomerId())
                  .customerType(Customer.RB)
                  .customerRelationShip(item.getRelationship())
                  .groupCode(Group.SPOUSE_LEGAL_PROFILE.getValue())
                  .build());
          continue;
        }
        lstRuleOTH.add(
            RuleRequest.builder()
                .rootGroup(Group.LEGAL_PROFILE.getValue())
                .domainType(CUSTOMER_TAG)
                .domainObjectId(customerEntity.getRefCustomerId())
                .customerType(Customer.RB)
                .customerRelationShip(item.getRelationship())
                .groupCode(Group.DEBT_GUARANTOR_LEGAL_PROFILE.getValue())
                .build());
      }
    }
    if (CollectionUtils.isNotEmpty(lstRuleNHP)) {
      lstRuleRequests.addAll(lstRuleNHP);
    }
    if (CollectionUtils.isNotEmpty(lstRuleOTH)) {
      lstRuleRequests.addAll(lstRuleOTH);
    }

    // Hồ sơ vay vốn: 02: LOAN_PROFILE
    List<RuleRequest> lstRuleLoan = buildLoanRule(entityApp);
    if (CollectionUtils.isNotEmpty(lstRuleLoan)) {
      lstRuleRequests.addAll(lstRuleLoan);
    }

    // Hồ sơ nguồn thu 03 : INCOME_PROFILE
    List<RuleRequest> lstRuleIncomes = buildIncomes(entityApp);
    if (CollectionUtils.isNotEmpty(lstRuleIncomes)) {
      lstRuleRequests.addAll(lstRuleIncomes);
    }

    // Hồ sơ tài sản 04 : ASSET_PROFILE
    List<RuleRequest> lstAssetRule = getAssetRuleList(entityApp, lstAssetRuleChecklist);
    if (CollectionUtils.isNotEmpty(lstAssetRule)) {
      lstRuleRequests.addAll(lstAssetRule);
    }

    // Hồ sơ phê duyệt: 05: APPROVAL_PROFILE
    lstRuleRequests.add(buildApprovalRule(entityApp));

    // Hồ sơ ngân hàng 06 : BANK_PROFILE
    lstRuleRequests.add(buildBankRule(entityApp));

    // Hồ sơ khác: 07: OTHER_PROFILE
    lstRuleRequests.add(buildOthers(entityApp));

    return GetChecklistRequest.builder()
        .requestCode(entityApp.getBpmId())
        .businessType(Customer.RB)
        .businessFlow(BusinessFlow.BO_USL_NRM.name())
        .phaseCode(PhaseCode.NRM_ALL_S.name())
        .isReused(false)
        .listRuleRequest(lstRuleRequests)
        .build();
  }

  private List<RuleRequest> getAssetRuleList(ApplicationEntity entityApp,
      List<AssetRuleChecklistResponse> lstAssetRuleChecklist) {
    List<RuleRequest> lstAssetRule;
    if (ObjectUtils.isNotEmpty(lstAssetRuleChecklist)) {
      lstAssetRule = buildAssetRule(entityApp, lstAssetRuleChecklist);
    } else {
      List<AssetRuleChecklistResponse> assetRuleChecklistResponses = collateralClient.getAssetRuleChecklist(
          entityApp.getBpmId());
      lstAssetRule = buildAssetRule(entityApp, assetRuleChecklistResponses);
    }
    return lstAssetRule;
  }

  public GetChecklistRequest buildChecklistAssetReq(ApplicationEntity entityApp,
      List<AssetRuleChecklistResponse> assetInfoRequests) {
    List<RuleRequest> lstRuleRequests = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(assetInfoRequests)) {
      assetInfoRequests
          .stream()
          .filter(assetInfoRequest -> Objects.nonNull(assetInfoRequest.getAssetId()))
          .forEach(assetInfoRequest ->
              lstRuleRequests.add(RuleRequest.builder()
                  .rootGroup(Group.ASSET_PROFILE.getValue())
                  .domainType(DomainType.COLLATERAL.name())
                  .domainObjectId(assetInfoRequest.getAssetId())
                  .customerType(Customer.RB)
                  .submissionPurpose(entityApp.getSubmissionPurpose())
                  .submissionMethod("V001")
                  .groupCode(
                      AssetGroup.mapGroupCategoryAsset().get(assetInfoRequest.getAssetGroup()))
                  .guarantee("N")
                  .assetGroup(assetInfoRequest.getAssetGroup())
                  .assetType(assetInfoRequest.getAssetType())
                  .isLoanPurpose(assetInfoRequest.getIsLoanPurpose() != null
                      && assetInfoRequest.getIsLoanPurpose() ? "Y" : "N")
                  .investorInformation(assetInfoRequest.getInvestorInformation())
                  .build()));
    }
    return GetChecklistRequest.builder()
        .requestCode(entityApp.getBpmId())
        .businessType(Customer.RB)
        .businessFlow(BusinessFlow.BO_USL_NRM.name())
        .phaseCode(PhaseCode.NRM_ALL_S.name())
        .isReused(false)
        .listRuleRequest(lstRuleRequests)
        .build();
  }

  private RuleRequest buildBankRule(ApplicationEntity entityApp) {
    return RuleRequest.builder()
        .rootGroup(Group.BANK_PROFILE.getValue())
        .domainType(DomainType.APPLICATION.name())
        .domainObjectId(entityApp.getId())
        .customerType(Customer.RB)
        .customerSegment(SegmentTypeEum.V003.name())
        .submissionPurpose(SubmissionPurpose.NEW_LEVEL.getCode())
        .groupCode(Group.BANK_PROFILE.getValue())
        .build();
  }

  private RuleRequest buildApprovalRule(ApplicationEntity entityApp) {
    return RuleRequest.builder()
        .rootGroup(APPROVAL_PROFILE.getValue())
        .domainType(DomainType.APPLICATION.name())
        .domainObjectId(entityApp.getId())
        .customerType(Customer.RB)
        .role(entityApp.getProcessingRole())
        .groupCode(APPROVAL_PROFILE.getValue())
        .build();
  }

  private List<RuleRequest> buildIncomes(ApplicationEntity entityApp) {
    List<RuleRequest> lstRuleIncomes = new ArrayList<>();

    for (ApplicationIncomeEntity item : entityApp.getIncomes()) {
      //Salary
      buildSalaryIncome(entityApp, item, lstRuleIncomes);
      //IndividualEnterprise
      buildIndividualEnterpriseIncome(entityApp, item, lstRuleIncomes);
      //Other
      buildOtherIncome(entityApp, item, lstRuleIncomes);
      //Rental
      buildRentalIncome(entityApp, item, lstRuleIncomes);
      //PropertyBusiness
      buildPropertyBusinessIncome(entityApp, item, lstRuleIncomes);
    }
    return lstRuleIncomes;
  }

  private void buildSalaryIncome(ApplicationEntity entityApp, ApplicationIncomeEntity item,
      List<RuleRequest> lstRuleIncomes) {
    if (CollectionUtils.isNotEmpty(item.getSalaryIncomes())) {
      item.getSalaryIncomes().stream()
          .forEach(
              e ->
                  lstRuleIncomes.add(
                      RuleRequest.builder()
                          .rootGroup(Group.INCOME_PROFILE.getValue())
                          .domainType(DomainType.INCOME.name())
                          .domainObjectId(e.getId())
                          .customerType(Customer.RB)
                          .submissionPurpose(entityApp.getSubmissionPurpose())
                          .submissionFlow(entityApp.getProcessFlow())
                          .incomeType(
                              Objects.nonNull(e.getIncomeType()) ? e.getIncomeType() : "V000")
                          .groupCode(INCOME_FROM_SALARY.getValue())
                          .build()));
    }
  }

  private void buildIndividualEnterpriseIncome(ApplicationEntity entityApp,
      ApplicationIncomeEntity item, List<RuleRequest> lstRuleIncomes) {
    if (CollectionUtils.isNotEmpty(item.getIndividualEnterpriseIncomes())) {
      item.getIndividualEnterpriseIncomes().stream()
          .forEach(
              e ->
                  lstRuleIncomes.add(
                      RuleRequest.builder()
                          .rootGroup(Group.INCOME_PROFILE.getValue())
                          .domainType(DomainType.INCOME.name())
                          .domainObjectId(e.getId())
                          .customerType(Customer.RB)
                          .submissionPurpose(entityApp.getSubmissionPurpose())
                          .submissionFlow(entityApp.getProcessFlow())
                          .incomeType(
                              Objects.nonNull(e.getIncomeType()) ? e.getIncomeType() : "V000")
                          .groupCode(
                              INDIVIDUAL_BUSINESS.equalsIgnoreCase(e.getIncomeType())
                                  ? INCOME_FROM_DN_HGD_HKD.getValue()
                                  : INCOME_FROM_ENTERPRISE.getValue())
                          .build()));
    }
  }

  private void buildOtherIncome(ApplicationEntity entityApp, ApplicationIncomeEntity item,
      List<RuleRequest> lstRuleIncomes) {
    if (CollectionUtils.isNotEmpty(item.getOtherIncomes())) {
      item.getOtherIncomes().stream()
          .forEach(
              e ->
                  lstRuleIncomes.add(
                      RuleRequest.builder()
                          .rootGroup(Group.INCOME_PROFILE.getValue())
                          .domainType(DomainType.INCOME.name())
                          .domainObjectId(e.getId())
                          .customerType(Customer.RB)
                          .submissionPurpose(entityApp.getSubmissionPurpose())
                          .submissionFlow(entityApp.getProcessFlow())
                          .incomeType(
                              Objects.nonNull(e.getIncomeType()) ? e.getIncomeType() : "V000")
                          .groupCode(INCOME_OTHER.getValue())
                          .build()));
    }
  }

  private void buildRentalIncome(ApplicationEntity entityApp, ApplicationIncomeEntity item,
      List<RuleRequest> lstRuleIncomes) {
    if (CollectionUtils.isNotEmpty(item.getRentalIncomes())) {
      item.getRentalIncomes().stream()
          .forEach(
              e ->
                  lstRuleIncomes.add(
                      RuleRequest.builder()
                          .rootGroup(Group.INCOME_PROFILE.getValue())
                          .domainType(DomainType.INCOME.name())
                          .domainObjectId(e.getId())
                          .customerType(Customer.RB)
                          .submissionPurpose(entityApp.getSubmissionPurpose())
                          .submissionFlow(entityApp.getProcessFlow())
                          .incomeType(
                              Objects.nonNull(e.getIncomeType()) ? e.getIncomeType() : "V000")
                          .groupCode(INCOME_FROM_RENTAL_ASSET.getValue())
                          .build()));
    }
  }

  private void buildPropertyBusinessIncome(ApplicationEntity entityApp,
      ApplicationIncomeEntity item,
      List<RuleRequest> lstRuleIncomes) {
    if (CollectionUtils.isNotEmpty(item.getPropertyBusinessIncomes())) {
      item.getPropertyBusinessIncomes().stream()
          .forEach(
              e ->
                  lstRuleIncomes.add(
                      RuleRequest.builder()
                          .rootGroup(Group.INCOME_PROFILE.getValue())
                          .domainType(DomainType.INCOME.name())
                          .domainObjectId(e.getId())
                          .customerType(Customer.RB)
                          .submissionPurpose(entityApp.getSubmissionPurpose())
                          .submissionFlow(entityApp.getProcessFlow())
                          .incomeType(
                              Objects.nonNull(e.getIncomeType()) ? e.getIncomeType() : "V000")
                          .groupCode(INCOME_FROM_PROPERTY_BUSINESS.getValue())
                          .build()));
    }
  }


  private List<RuleRequest> buildAssetRule(ApplicationEntity entityApp,
      List<AssetRuleChecklistResponse> assetRuleChecklistResponses) {
    List<RuleRequest> lstAssetRules = new ArrayList<>();
    if (CollectionUtils.isEmpty(assetRuleChecklistResponses)) {
      return Collections.emptyList();
    }

    assetRuleChecklistResponses
        .stream()
        .filter(assetInfoDto -> Objects.nonNull(assetInfoDto.getAssetId()))
        .forEach(assetInfoDto ->{
            if(ObjectUtils.isNotEmpty(AssetGroup.mapGroupCategoryAsset().get(assetInfoDto.getAssetGroup()))) {
              lstAssetRules.add(RuleRequest.builder()
                      .rootGroup(Group.ASSET_PROFILE.getValue())
                      .domainType(DomainType.COLLATERAL.name())
                      .domainObjectId(assetInfoDto.getAssetId())
                      .customerType(Customer.RB)
                      .submissionPurpose(entityApp.getSubmissionPurpose())
                      .submissionMethod("V001")
                      .groupCode(AssetGroup.mapGroupCategoryAsset().get(assetInfoDto.getAssetGroup()))
                      .guarantee("N")
                      .assetGroup(assetInfoDto.getAssetGroup())
                      .assetType(assetInfoDto.getAssetType())
                      .isLoanPurpose(
                              assetInfoDto.getIsLoanPurpose() != null && assetInfoDto.getIsLoanPurpose() ? "Y"
                                      : "N")
                      .investorInformation(assetInfoDto.getInvestorInformation())
                      .build());
            }});
    log.info(
        "buildAssetRule END with bpmId={}, lstAssetRules={}",
        entityApp.getBpmId(),
        JsonUtil.convertObject2String(lstAssetRules, objectMapper));
    return lstAssetRules;
  }

  private List<RuleRequest> buildLoanRule(ApplicationEntity entityApp) {
    List<RuleRequest> listRuleLoan = new ArrayList<>();
    for (ApplicationCreditEntity item : entityApp.getCredits()) {
      if (Objects.nonNull(item.getCreditCard())
          || Objects.nonNull(item.getCreditLoan())
          || Objects.nonNull(item.getCreditOverdraft())) {
        String loanPurpose = "V000";
        if (Objects.nonNull(item.getCreditLoan())) {
          loanPurpose = item.getCreditLoan().getLoanPurpose();
        }
        if (Objects.nonNull(item.getCreditOverdraft())) {
          loanPurpose = item.getCreditOverdraft().getLoanPurpose();
        }
        listRuleLoan.add(
            RuleRequest.builder()
                .rootGroup(LOAN_PROFILE.getValue())
                .domainType(DomainType.APPLICATION.name())
                .domainObjectId(entityApp.getId())
                .customerType(Customer.RB)
                .submissionPurpose(entityApp.getSubmissionPurpose())
                .loanPurpose(loanPurpose)
                .groupCode(LOAN_PROFILE.getValue())
                .build());
      }
    }
    return listRuleLoan;
  }

  private RuleRequest buildOthers(ApplicationEntity entityApp) {
    return RuleRequest.builder()
        .rootGroup(OTHER_PROFILE.getValue())
        .domainType(DomainType.APPLICATION.name())
        .domainObjectId(entityApp.getId())
        .customerType(Customer.RB)
        .customerSegment(Customer.RB)
        .submissionPurpose(Customer.RB)
        .groupCode(OTHER_PROFILE.getValue())
        .build();
  }

  @Transactional
  public void saveDBChecklist(Long appId, String checklistMappingId) {
    ApplicationChecklistEntity entityChecklist =
        ApplicationChecklistEntity.builder()
            .applicationId(appId)
            .phase(PhaseCode.NRM_ALL_S.name())
            //            .checklistMappingId(checklistMappingId)
            .build();
    applicationChecklistRepository.save(entityChecklist);
  }

  @Transactional(readOnly = true)
  public void getDecisionRule(Long applicationId, RuleCode ruleCode, GetChecklistRequest request) {
    log.info("Mapping checklist rule version for applicationId {} , ruleCode {}", applicationId, ruleCode);
    if (ruleCode == null) {
      log.error("Mapping checklist rule version fail , because don't have ruleCode");
      throw new ApprovalException(DomainCode.RULE_CODE_MUST_NOT_NULL);
    }

    List<RuleVersionMappingEntity> ruleVersionMapping =
        ruleVersionMappingRepository
            .findByApplicationIdAndRuleCode(applicationId, ruleCode)
            .orElse(null);

    if (CollectionUtils.isEmpty(ruleVersionMapping)) {
      log.info(
          "Rule {} has never been used in application {}, get rule with latest version...",
          ruleCode,
          applicationId);
    } else {
//      log.info(
//          "Rule {} was used before in application {}, get rule with version {}",
//          ruleCode,
//          applicationId,
//          ruleVersionMapping.stream().collect(Collectors.toMap(RuleVersionMappingEntity::getGroupCode, RuleVersionMappingEntity::getRuleVersion)));
      for (String groupCode :
          request.getListRuleRequest().stream()
              .map(RuleRequest::getGroupCode)
              .filter(Objects::nonNull)
              .distinct()
              .collect(Collectors.toList())) {
        RuleVersionMappingEntity ruleVersionMappingEntity =
            ruleVersionMapping.stream()
                .filter(e -> groupCode.equals(e.getGroupCode()))
                .findFirst()
                .orElse(null);
        if (Objects.nonNull(ruleVersionMappingEntity)) {
          // set version
          request.getListRuleRequest().stream()
              .filter(e -> groupCode.equalsIgnoreCase(e.getGroupCode()))
              .forEach(
                  e ->
                      e.setRuleVersion(
                          Objects.nonNull(ruleVersionMappingEntity.getRuleVersion())
                              ? Long.valueOf(ruleVersionMappingEntity.getRuleVersion())
                              : null));
        }
      }
    }
  }
}
