package com.msb.bpm.approval.appr.service.cms.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.UNFINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.CMS_PUSH_DOCUMENTS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.camunda.CamundaClient;
import com.msb.bpm.approval.appr.config.properties.MinioProperties;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.enums.checklist.BusinessFlow;
import com.msb.bpm.approval.appr.enums.checklist.DomainType;
import com.msb.bpm.approval.appr.enums.cms.BucketType;
import com.msb.bpm.approval.appr.enums.common.LdpConfirmStatus;
import com.msb.bpm.approval.appr.enums.common.LdpStatus;
import com.msb.bpm.approval.appr.enums.common.PhaseCode;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.FileDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryFeedbackEntity;
import com.msb.bpm.approval.appr.model.entity.RuleVersionMappingEntity;
import com.msb.bpm.approval.appr.model.request.checklist.CreateChecklistRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2DocumentsRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2DocumentsRequest.Document;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.cms.CmsBaseResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse;
import com.msb.bpm.approval.appr.notication.NoticeType;
import com.msb.bpm.approval.appr.notication.NotificationFactory;
import com.msb.bpm.approval.appr.notication.NotificationInterface;
import com.msb.bpm.approval.appr.repository.ApplicationDraftRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.RuleVersionMappingRepository;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.application.impl.PostCreateApplicationServiceImpl;
import com.msb.bpm.approval.appr.service.checklist.impl.ChecklistServiceImpl;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 21/8/2023, Monday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class CmsPushDocumentsServiceImpl implements BaseService<CmsBaseResponse, PostCmsV2DocumentsRequest> {
  private final CmsBaseIntegrationService  cmsBaseIntegrationService;
  private final CommonService commonService;
  private final ObjectMapper objectMapper;
  private final RuleVersionMappingRepository ruleVersionMappingRepository;
  private final ChecklistServiceImpl checklistService;
  private final MinioProperties minioProperties;
  private final ApplicationRepository applicationRepository;
  private final CamundaClient camundaClient;
  private final PostCreateApplicationServiceImpl postCreateApplicationService;
  private final NotificationFactory factory;
  private final ApplicationDraftRepository applicationDraftRepository;


  @Override
  public String getType() {
    return CMS_PUSH_DOCUMENTS;
  }

  @Override
  @Transactional
  public CmsBaseResponse execute(PostCmsV2DocumentsRequest request, Object... params) {
    String bpmApplicationId = (String) params[0];
    log.info("CmsPushDocumentsServiceImpl.execute start with bpmId : {} , request : {}", bpmApplicationId, JsonUtil.convertObject2String(request, objectMapper));
    ApplicationEntity entityApp = commonService.findAppByBpmId(bpmApplicationId);

    if (LdpStatus.CUSTOMER_EDITED.getValue().equals(entityApp.getLdpStatus())) {
      log.error(
          "Application {} is being processed, status {} , ldp status {} , cannot update/add information...",
          bpmApplicationId, entityApp.getStatus(), entityApp.getLdpStatus());
      throw new ApprovalException(DomainCode.UPDATE_APPLICATION_ERROR_BY_LDP_STATUS,
          new Object[]{bpmApplicationId});
    }

    if (request.getDocuments() != null && request.getDocuments().size() > 0) {
      // Xử lý call save checklist
      Map<String, Object> mapSizeFile = cmsBaseIntegrationService.getSizeFileAll(
          entityApp.getBpmId(), request.getDocuments());
      List<String> lstFileNotExist = cmsBaseIntegrationService.checkIfNotExistFile(
          request.getDocuments(), mapSizeFile, entityApp.getBpmId());
      if (CollectionUtils.isNotEmpty(lstFileNotExist)) {
        throw new ApprovalException(DomainCode.NOT_EXIST_FILES_IN_MINIO,
            new Object[]{lstFileNotExist});
      }
      saveChecklist(entityApp, request, mapSizeFile);
    }

    boolean isStartCamunda = false;
    if (entityApp.getProcessInstance() == null) {
      isStartCamunda = true;
    } else {
      try {
        camundaClient.getProcessInstanceById(
            entityApp.getProcessInstance().getProcessInstanceId());
      } catch (Exception ex) {
        log.error("Not found Process Instance with bpmId : {} , error: ",
            bpmApplicationId,
            ex);
        isStartCamunda = true;
      }
    }

    // Check Status Application & process_instance_id
    if (isStartCamunda) {
      // Camunda start instance workflow
      postCreateApplicationService.startCamundaInstance(entityApp);
      log.info("Start instance camunda for bpmId : {}", bpmApplicationId);
    }

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("currentLdpStatus", entityApp.getLdpStatus());
    metadata.put("isStartCamunda", isStartCamunda);

    entityApp.setLdpStatus(LdpStatus.CUSTOMER_EDITED.getValue());

    saveReason(request.getConfirmStatus(), request.getReason(), entityApp);

    applicationRepository.saveAndFlush(entityApp);

    // notification
    NotificationInterface notificationInterface = factory.getNotification(NoticeType.LDP);
    notificationInterface.notice(entityApp.getCustomer().getIndividualCustomer().getLastName() + " " + entityApp.getCustomer().getIndividualCustomer().getFirstName(), entityApp.getBpmId(), entityApp.getAssignee());

    CmsBaseResponse response = CmsBaseResponse.builder().bpmId(bpmApplicationId).status(entityApp.getStatus())
        .metadata(metadata).build();

    log.info("CmsPushDocumentsServiceImpl.execute end with bpmId : {} , response : {}", bpmApplicationId, JsonUtil.convertObject2String(response, objectMapper));

    return response;
  }


  public void saveChecklist(ApplicationEntity entityApp, PostCmsV2DocumentsRequest request, Map<String, Object> mapSizeFile) {
    log.info("CmsPushDocumentsServiceImpl saveChecklist START with bpmId={}, request={}, mapSizeFile={}", entityApp.getBpmId(), JsonUtil.convertObject2String(request, objectMapper), mapSizeFile);
    try {
      List<RuleVersionMappingEntity> ruleVersionMapping = ruleVersionMappingRepository.findByApplicationIdAndRuleCode(entityApp.getId(), RuleCode.R001).orElse(null);
      List<ChecklistDto> listChecklistDto = new ArrayList<>();
      ChecklistBaseResponse<GroupChecklistDto> checklistBaseResponse = checklistService.getChecklistByRequestCode(entityApp.getBpmId());
      log.info("CmsPushDocumentsServiceImpl saveChecklist START with bpmId={}, checklistBaseResponse={}", entityApp.getBpmId(),  JsonUtil.convertObject2String(checklistBaseResponse, objectMapper));
      for (Document item : request.getDocuments()) {
        Long ruleVersion = null;
        if (CollectionUtils.isNotEmpty(ruleVersionMapping)) {
          RuleVersionMappingEntity ruleVersionMappingEntity = ruleVersionMapping.stream().filter(e ->
              item.getGroup().equals(e.getGroupCode())).findFirst().orElse(null);
          ruleVersion = Objects.nonNull(ruleVersionMappingEntity) && Objects.nonNull(
              ruleVersionMappingEntity.getRuleVersion()) ?
              Long.valueOf(ruleVersionMappingEntity.getRuleVersion()) : null;
        }

        ChecklistDto checklistDto = ChecklistDto.builder()
            .code(item.getDocCode())
            .name(item.getDocName())
            .groupCode(item.getGroup())
            .isError(false)
            .isRequired(true)
            .isGenerated(false)
            .ruleVersion(ruleVersion)
            .build();

        boolean status = cmsBaseIntegrationService.setAdditionIdAndChecklistMappingId(checklistBaseResponse, checklistDto, item.getCmsDomainReferenceId(), entityApp);
        if (!status) continue;

        List<FileDto> listFile = new ArrayList<>();
        item.getFiles().forEach(file ->  {
          String minIOPath = file.getMinioPath()
              .replace(minioProperties.getMinio().getBucketExt().get(BucketType.CMS.getValue()).getBucket() + ApplicationConstant.SEPARATOR, "");
          listFile.add(new FileDto()
              .withBucket(minioProperties.getMinio().getBucket())
              .withMinioPath(minIOPath)
              .withFileName(file.getFileName())
              .withFileType(Util.getExtensionFile(file.getFileName()))
              .withFileSize(cmsBaseIntegrationService.getSizeFileFromMap(file.getMinioPath(), mapSizeFile))
          );
        });

        List<FileDto> finalFiles = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(checklistDto.getListFile())) {
          for (FileDto file : listFile) {
            checklistDto.getListFile().stream()
                .filter(x -> x.getFileName().equals(file.getFileName()))
                .findFirst().ifPresent(oldFile -> file.setId(oldFile.getId()));
            finalFiles.add(file);
          }
        } else {
          finalFiles.addAll(listFile);
        }

        log.info("saveChecklist  with bpmId={}, checklistDto={}", entityApp.getBpmId(), JsonUtil.convertObject2String(checklistDto, objectMapper));
        if (Objects.nonNull(checklistDto.getChecklistMappingId())
            && Objects.nonNull(checklistDto.getAdditionalDataChecklistId())) {
          checklistDto.setListFile(finalFiles);
          listChecklistDto.add(checklistDto);
        }
      }



      log.info("saveChecklist START with bpmId={}, listChecklistDto={}", entityApp.getBpmId(), JsonUtil.convertObject2String(listChecklistDto, objectMapper));
      CreateChecklistRequest createChecklistRequest = CreateChecklistRequest.builder()
              .requestCode(entityApp.getBpmId())
              .businessFlow(BusinessFlow.BO_USL_NRM.name())
              .phaseCode(PhaseCode.NRM_ALL_S.name())
              .listChecklist(listChecklistDto)
              .build();
      Object resultChecklist = checklistService.uploadFileChecklist(createChecklistRequest);
      log.info("saveChecklist END with request={}, resultChecklist={}, mapSizeFile={}",
          JsonUtil.convertObject2String(request, objectMapper),
          JsonUtil.convertObject2String(resultChecklist, objectMapper), mapSizeFile);

      // Lưu lại thông tin checklist tài sản
      saveLogCheckListAsset(entityApp.getBpmId(), (ChecklistBaseResponse<GroupChecklistDto>) resultChecklist);
    } catch (Exception ex) {
      log.error("saveChecklist with request={}, Error: ",
          JsonUtil.convertObject2String(request, objectMapper), ex);
      throw ex;
    }

  }

  private void saveLogCheckListAsset(String bpmId, ChecklistBaseResponse<GroupChecklistDto> checklists) {
    try {
      if (checklists.getData() != null && ObjectUtils.isNotEmpty(checklists.getData().getListChecklist())) {
        List<ChecklistDto> listChecklistAsset = new ArrayList<>();
        for (ChecklistDto checklistItem : checklists.getData().getListChecklist()) {
          if (checklistItem.getDomainType() != null && DomainType.COLLATERAL.name().equals(checklistItem.getDomainType())
                  && ObjectUtils.isNotEmpty(checklistItem.getListFile())
          ) {
            listChecklistAsset.add(checklistItem);
          }
        }
        if (ObjectUtils.isNotEmpty(listChecklistAsset)) {
          ApplicationDraftEntity draftEntity = applicationDraftRepository.findByBpmIdAndTabCode(bpmId, ApplicationConstant.TabCode.CHECKLIST_ASSET_INFO)
                  .orElse(null);
          if (draftEntity != null) {
            draftEntity.setData(JsonUtil.convertObject2String(listChecklistAsset, objectMapper).getBytes(StandardCharsets.UTF_8));
            draftEntity.setStatus(UNFINISHED);
            applicationDraftRepository.save(draftEntity);
          } else {
            persistApplicationDraft(bpmId, ApplicationConstant.TabCode.CHECKLIST_ASSET_INFO, UNFINISHED, listChecklistAsset);
          }
        }
      }
    } catch (Exception e) {
      log.error("saveLogCheckListAsset error ", e);
    }
  }

  public <T> void persistApplicationDraft(String bpmId, String tabCode, int status, T t) {
    ApplicationDraftEntity applicationDraft = applicationDraftRepository.findByBpmIdAndTabCode(bpmId, tabCode).orElse(null);
    if (applicationDraft == null) {
      applicationDraft = new ApplicationDraftEntity();
      applicationDraft.setBpmId(bpmId);
      applicationDraft.setTabCode(tabCode);
    }
    applicationDraft.setStatus(status);
    applicationDraft.setData(
            JsonUtil.convertObject2String(t, objectMapper).getBytes(StandardCharsets.UTF_8));
    applicationDraftRepository.save(applicationDraft);
  }

  public void saveReason(LdpConfirmStatus confirmStatus, String reason, ApplicationEntity applicationEntity) {
    if (LdpConfirmStatus.CONFIRM.equals(confirmStatus)) {
      return;
    }

    GetUserProfileResponse userProfileResponse = commonService.getUserDetail(
        SecurityContextUtil.getCurrentUser());

    ApplicationHistoryFeedbackEntity feedbackEntity = new ApplicationHistoryFeedbackEntity();
    feedbackEntity.setUserRole(applicationEntity.getSource());
    feedbackEntity.setApplication(applicationEntity);
    feedbackEntity.setFeedbackAt(LocalDateTime.now());
    feedbackEntity.setUsername(SecurityContextUtil.getCurrentUser());
    if (StringUtils.isNotBlank(reason)) {
      feedbackEntity.setComment(reason.getBytes(StandardCharsets.UTF_8));
    }
    feedbackEntity.setCreatedPhoneNumber(userProfileResponse.getUser().getPhoneNumber());
    applicationEntity.getHistoryFeedbacks().add(feedbackEntity);
  }
}
