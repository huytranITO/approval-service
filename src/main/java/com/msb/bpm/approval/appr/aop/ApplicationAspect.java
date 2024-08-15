package com.msb.bpm.approval.appr.aop;

import static com.msb.bpm.approval.appr.aop.EventType.CMS_INFO;
import static com.msb.bpm.approval.appr.aop.EventType.CMS_V2_INFO;
import static com.msb.bpm.approval.appr.aop.EventType.GENERAL_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.FINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.UNFINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_ID;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_IDS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.ASSIGNEE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.LDP_CONFIRM_STATUS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.LDP_REASON;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.REASONS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.TOKEN;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.ASSIGN_APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_ASSIGN_TO_TL;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CHECK_LIST_TAB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CLOSE_APP;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_DEBT_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_FIELD_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_INITIALIZE_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_REWORK_APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_SUBMIT_APP;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_ASSET_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DEBT_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.FIELD_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.INITIALIZE_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.ASSET_INFO;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.AS9999;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.isDone;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.isNotRefreshTabStatus;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.CLOSE_APPLICATION;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.FLOW_COMPLETE;
import static com.msb.bpm.approval.appr.enums.email.EventCodeEmailType.TEAM_LEAD_ASSIGN_APPROVER;
import static com.msb.bpm.approval.appr.enums.email.EventCodeEmailType.TEAM_LEAD_ASSIGN_CONTACT;
import static com.msb.bpm.approval.appr.util.Util.mapTabCode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.chat.events.AddUserToRoomEvent;
import com.msb.bpm.approval.appr.client.chat.SmartChatClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.constant.MessageCode;
import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.application.ProcessingStep;
import com.msb.bpm.approval.appr.enums.common.LdpConfirmStatus;
import com.msb.bpm.approval.appr.enums.common.LdpStatus;
import com.msb.bpm.approval.appr.enums.email.EventCodeEmailType;
import com.msb.bpm.approval.appr.enums.email.NoticeFeedback;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.kafka.BeanProducer;
import com.msb.bpm.approval.appr.kafka.producer.CmsInfoProducerStrategy;
import com.msb.bpm.approval.appr.kafka.producer.CmsV2InfoProducerStrategy;
import com.msb.bpm.approval.appr.kafka.producer.GeneralInfoProducerStrategy;
import com.msb.bpm.approval.appr.model.dto.AmlOprDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationApprovalHistoryDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDTO;
import com.msb.bpm.approval.appr.model.dto.CicDTO;
import com.msb.bpm.approval.appr.model.dto.DebtInfoDTO;
import com.msb.bpm.approval.appr.model.dto.FieldInforDTO;
import com.msb.bpm.approval.appr.model.dto.GetApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.InitializeInfoDTO;
import com.msb.bpm.approval.appr.model.dto.AssetCommonInfoDTO;
import com.msb.bpm.approval.appr.model.dto.application.HistoryApprovalDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoricIntegration;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.model.request.chat.AddUserToRoomRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2DocumentsRequest;
import com.msb.bpm.approval.appr.model.request.data.PostAssetInfoRequest;
import com.msb.bpm.approval.appr.model.request.data.PostDebtInfoRequest;
import com.msb.bpm.approval.appr.model.request.data.PostInitializeInfoRequest;
import com.msb.bpm.approval.appr.model.request.feeback.PostApplicationFeedbackRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostAssignRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostCloseApplicationRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostReworkApplicationRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostSubmitRequest;
import com.msb.bpm.approval.appr.model.response.MessageResponse;
import com.msb.bpm.approval.appr.model.response.cms.CmsBaseResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.history.ApplicationHistoryApprovalService;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Aspect
@Configuration
@Slf4j
@RequiredArgsConstructor
public class ApplicationAspect extends AbstractBaseService {

  private final ApplicationHistoryApprovalService approvalHistoryService;
  private final MessageSource messageSource;
  private final AopProxy aop;
  private final ObjectMapper objectMapper;
  private final CollateralClient collateralClient;
  private final GeneralInfoProducerStrategy generalInfoProducerStrategy;
  private final CmsInfoProducerStrategy cmsInfoProducerStrategy;
  private final CmsV2InfoProducerStrategy cmsV2InfoProducerStrategy;
  private final ApplicationEventPublisher applicationEventPublisher;

  private final ApplicationRepository applicationRepository;


  /**
   * Ghi đè dữ liệu lưu tạm nếu hồ sơ đang ở state lưu tạm
   *
   * @param joinPoint ProceedingJoinPoint
   */
  @Around("execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.getApplication(..))")
  public Object replaceDraftInfoAfterReturning(ProceedingJoinPoint joinPoint) throws Throwable {
    GetApplicationDTO response = (GetApplicationDTO) joinPoint.proceed();

    Map<Integer, Set<ApplicationDraftEntity>> draftMapByStatus = draftMapByStatus(
        response.getInitializeInfo().getApplication().getBpmId());

    draftMapByStatus.forEach((k, v) -> {
      if (UNFINISHED == k) {
        fillTabUnCompleted(response, v);
      } else {
        fillTabCompleted(response, v);
      }
    });

    return response;
  }

  private void fillTabUnCompleted(GetApplicationDTO applicationDTO,
      Set<ApplicationDraftEntity> applicationDrafts) {
    if (CollectionUtils.isEmpty(applicationDrafts)) {
      return;
    }
    applicationDrafts.forEach(
        applicationDraft -> replaceApplicationDraftData(applicationDTO, applicationDraft));
  }

  private void fillTabCompleted(GetApplicationDTO applicationDTO,
      Set<ApplicationDraftEntity> applicationDrafts) {
    if (CollectionUtils.isEmpty(applicationDrafts)) {
      return;
    }
    applicationDrafts.forEach(applicationDraft -> {
      switch (applicationDraft.getTabCode()) {
        case INITIALIZE_INFO:
          setTabCompleted(applicationDTO.getInitializeInfo());
          break;
        case FIELD_INFO:
          setTabCompleted(applicationDTO.getFieldInfor());
          break;
        case DEBT_INFO:
          setTabCompleted(applicationDTO.getDebtInfo());
          break;
        case ASSET_INFO:
          setTabCompleted(applicationDTO.getAssetInfo());
          break;
        default:
          break;
      }
    });
  }

  /**
   * Cập nhật state lưu tạm sau khi thực hiện lưu dữ liệu xuống database
   *
   * @param joinPoint ProceedingJoinPoint
   */
  @Around("execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postSaveData(..))")
  public Object updateDraftStatusAfterSaveSuccess(ProceedingJoinPoint joinPoint)
      throws Throwable {
    Object response = joinPoint.proceed();

    String bpmId = (String) joinPoint.getArgs()[0];
    PostBaseRequest request = (PostBaseRequest) joinPoint.getArgs()[1];
    if(response instanceof InitializeInfoDTO) {
      ((InitializeInfoDTO) response).setType(POST_INITIALIZE_INFO);
    }

    if(response instanceof DebtInfoDTO) {
      ((DebtInfoDTO) response).setType(POST_DEBT_INFO);
    }

    if(response instanceof FieldInforDTO) {
      ((FieldInforDTO) response).setType(POST_FIELD_INFO);
    }

    if(response instanceof AssetCommonInfoDTO) {
      ((AssetCommonInfoDTO) response).setType(POST_ASSET_INFO);
    }

    if (!POST_CHECK_LIST_TAB.equals(request.getType())) {
      persistApplicationDraft(bpmId, mapTabCode(request.getType()), FINISHED, response);

      setTabCompleted(response);
    }

    return response;
  }

  /**
   * Lưu lịch sử yêu cầu của hồ sơ
   *
   * @param joinPoint ProceedingJoinPoint
   * @return Object
   */
  @Order(100)
  @Transactional
  @Around("execution(* com.msb.bpm.approval.appr.service.application.impl.PostSubmitApplicationServiceImpl.execute(..))"
      + " || execution(* com.msb.bpm.approval.appr.service.application.impl.AssignApplicationServiceImpl.setAssignee(..))"
      + " || execution(* com.msb.bpm.approval.appr.service.application.impl.PostCloseApplicationServiceImpl.execute(..))"
      + " || execution(* com.msb.bpm.approval.appr.service.application.impl.PostCoordinatorAssignToTLServiceImpl.execute(..))"
      + " || execution(* com.msb.bpm.approval.appr.service.application.impl.PostCreateApplicationServiceImpl.execute(..))"
      + " || execution(* com.msb.bpm.approval.appr.service.application.impl.PostReworkApplicationServiceImpl.execute(..))"
      + " || execution(* com.msb.bpm.approval.appr.service.cms.impl.CmsIntegrationServiceImpl.saveApplication(..))")
  public Object saveNewHistoryApproval(ProceedingJoinPoint joinPoint) throws Throwable {
    ApplicationEntity applicationEntity = (ApplicationEntity) joinPoint.proceed();

    Long applicationId = applicationEntity.getId();
    String processingRole = applicationEntity.getProcessingRole();
    String assignee = applicationEntity.getAssignee();
    String status = applicationEntity.getStatus();
    String processingStep = applicationEntity.getProcessingStep();
    String previousRole = applicationEntity.getPreviousRole();

    executeHistoryApproval(joinPoint, applicationId, processingRole, previousRole, assignee, status,
            processingStep);

    return applicationEntity;
  }

  @Transactional
  public void executeHistoryApproval(ProceedingJoinPoint joinPoint, Long applicationId,
      String processingRole, String previousRole, String assignee, String status, String processingStep) {
    try {
      log.info("Save new history approval...");

      HistoryApprovalDTO historyApprovalDTO = new HistoryApprovalDTO();
      historyApprovalDTO.setUserRole(processingRole);
      historyApprovalDTO.setUserName(assignee);
      historyApprovalDTO.setApplicationId(applicationId);
      historyApprovalDTO.setFromUserRole(previousRole);
      historyApprovalDTO.setStep(getHistoryProcessingStep(status, processingStep,
          ((BaseService) joinPoint.getTarget()).getType()));
      historyApprovalDTO.setStatus(status);

      Object[] args = joinPoint.getArgs();

      switch (((BaseService) joinPoint.getTarget()).getType()) {
        case ASSIGN_APPLICATION:
          String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
          for (int i = 0; i < parameterNames.length; i++) {
            if ("receptionUser".equalsIgnoreCase(parameterNames[i])) {
              historyApprovalDTO.setProposalApprovalUser((String) args[i]);
              break;
            }
          }
          break;
        case POST_CLOSE_APP:
          historyApprovalDTO.setNote(((PostCloseApplicationRequest) args[0]).getNote());
          historyApprovalDTO.setReasons(((PostCloseApplicationRequest) args[0]).getReasons());
          break;
        case POST_ASSIGN_TO_TL:
          historyApprovalDTO.setProposalApprovalUser(assignee);
          break;
        case POST_REWORK_APPLICATION:
          historyApprovalDTO.setReasons(((PostReworkApplicationRequest) args[0]).getReasons());
          historyApprovalDTO.setProposalApprovalUser(
              ((PostReworkApplicationRequest) args[0]).getReceptionUser());
          break;
        case POST_SUBMIT_APP:
          PostBaseRequest request = (PostBaseRequest) args[0];
          if (request instanceof PostSubmitRequest) {
            historyApprovalDTO.setProposalApprovalReception(
                ((PostSubmitRequest) args[0]).getProposalApprovalReception());
            historyApprovalDTO.setProposalApprovalUser(
                ((PostSubmitRequest) args[0]).getReceptionUser());
          }
          break;
        default:
          break;
      }

      approvalHistoryService.saveHistoryApproval(historyApprovalDTO);
    } catch (Exception e) {
      log.error("Save new history approval failure: ", e);
    }
  }

  private String getHistoryProcessingStep(String status, String processingStep, String methodType) {
    String step;

    if (POST_SUBMIT_APP.equalsIgnoreCase(methodType) && AS9999.getValue().equalsIgnoreCase(status)) {
      step = messageSource.getMessage(FLOW_COMPLETE.getValue(), null, Util.locale());
    } else if (POST_CLOSE_APP.equalsIgnoreCase(methodType)) {
      step = messageSource.getMessage(CLOSE_APPLICATION.getValue(), null, Util.locale());
    } else {
      step = processingStep;
    }

    return step;
  }

  /**
   * Cập nhật dữ liệu tra cứu AML, OPR, CIC, Xếp hạng tín dụng vào trạng thái lưu tạm hồ sơ
   *
   * @param joinPoint
   * @return
   * @throws Throwable
   */
  @Around("execution(* com.msb.bpm.approval.appr.service.application.impl.PostSyncAmlOprInfoServiceImpl.execute(..))"
      + " || execution(* com.msb.bpm.approval.appr.service.application.impl.PostQueryCICInfoServiceImpl.execute(..))"
      + " || execution(* com.msb.bpm.approval.appr.service.application.impl.PostCreditRatingCSSServiceImpl.execute(..))"
      + " || execution(* com.msb.bpm.approval.appr.service.application.impl.PostCreditRatingCSSV2ServiceImpl.execute(..))"
  )
  public Object updateApplicationDraftData(ProceedingJoinPoint joinPoint) throws Throwable {
    Object response = joinPoint.proceed();

    Object[] args = joinPoint.getArgs();
    Object[] params = (Object[]) args[1];
    String bpmId = (String) params[0];

    Map<String, ApplicationDraftEntity> mapDataDraft = draftMapByTabCode(bpmId);

    if (response instanceof AmlOprDTO) {
      appendAmlOpToApplicationDraft(mapDataDraft, response, bpmId);
    } else if (response instanceof CicDTO) {
      appendCicToApplicationDraft(mapDataDraft, response, bpmId);
    } else if (response instanceof ApplicationCreditRatingsDTO) {
      appendCreditRatingsToApplicationDraft(mapDataDraft, response, bpmId);
    }

    return response;
  }

  private void appendAmlOpToApplicationDraft(Map<String, ApplicationDraftEntity> mapDataDraft, Object response, String bpmId) {
    ApplicationDraftEntity applicationDraftEntity = mapDataDraft.get(INITIALIZE_INFO);
    if (applicationDraftEntity.getData() != null && UNFINISHED == applicationDraftEntity.getStatus()) {
      PostInitializeInfoRequest request = (PostInitializeInfoRequest) getDraftData(applicationDraftEntity, INITIALIZE_INFO);
      request.getCustomerAndRelationPerson().setAmlOpr((AmlOprDTO) response);
      persistApplicationDraft(bpmId, INITIALIZE_INFO, UNFINISHED, request);
    }
  }

  private void appendCicToApplicationDraft(Map<String, ApplicationDraftEntity> mapDataDraft, Object response, String bpmId) {
    ApplicationDraftEntity applicationDraftEntity = mapDataDraft.get(INITIALIZE_INFO);
    if (applicationDraftEntity.getData() != null && UNFINISHED == applicationDraftEntity.getStatus()) {
      PostInitializeInfoRequest request = (PostInitializeInfoRequest) getDraftData(applicationDraftEntity, INITIALIZE_INFO);
      request.getCustomerAndRelationPerson().setCic((CicDTO) response);
      persistApplicationDraft(bpmId, INITIALIZE_INFO, UNFINISHED, request);
    }
  }

  private void appendCreditRatingsToApplicationDraft(Map<String, ApplicationDraftEntity> mapDataDraft, Object response, String bpmId) {
    ApplicationDraftEntity applicationDraftEntity = mapDataDraft.get(DEBT_INFO);
    if (applicationDraftEntity.getData() != null && UNFINISHED == applicationDraftEntity.getStatus()) {
      PostDebtInfoRequest request = (PostDebtInfoRequest) getDraftData(applicationDraftEntity, DEBT_INFO);
      if (CollectionUtils.isEmpty(request.getCreditRatings())) {
        request.setCreditRatings(Collections.singleton((ApplicationCreditRatingsDTO)response));
      } else {
        request.getCreditRatings().add((ApplicationCreditRatingsDTO)response);
      }
      persistApplicationDraft(bpmId, DEBT_INFO, UNFINISHED, request);
    }
  }

  /**
   * Refresh trạng thái tích xanh sau khi đệ trình hồ sơ/hoàn thành
   *
   * @param joinPoint ProceedingJoinPoint
   * @return Object
   * @throws Throwable
   */
  @Order(101)
  @Around("execution(* com.msb.bpm.approval.appr.service.application.impl.PostSubmitApplicationServiceImpl.execute(..)) " +
          "|| execution(* com.msb.bpm.approval.appr.service.application.impl.PostReworkApplicationServiceImpl.execute(..))" +
          "|| execution(* com.msb.bpm.approval.appr.service.application.impl.PostCloseApplicationServiceImpl.execute(..))" +
          "|| execution(* com.msb.bpm.approval.appr.service.application.impl.AssignApplicationServiceImpl.setAssignee(..))")
  public Object refreshTabStatus(ProceedingJoinPoint joinPoint) throws Throwable {
    ApplicationEntity response = (ApplicationEntity) joinPoint.proceed();

    if (!isNotRefreshTabStatus(response.getProcessingRole())
        && !isDone(response.getStatus())) {
      refreshApplicationDataDraft(response.getBpmId());
      collateralClient.refreshStatusApplicationDraft(response.getBpmId());
    }

    return response;
  }

  /**
   * Push thông tin hồ sơ ra kafka
   *
   * - pushToGeneralInfo : đẩy thông tin hồ sơ lên khay (General Info)
   * - pushToBroadcast : đẩy thông tin trạng thái hồ sơ ra broadcast (v1)
   * - pushToBroadcastV2 : đẩy thông tin trạng thái hồ sơ ra broadcast (v2)
   * - processInstanceService.updateProcessInstance(bpmIds) : lấy next user task camunda sau khi thực hiện đệ trình/hoàn thành
   *
   * @param joinPoint
   * @return
   * @throws Throwable
   */
  @Order(1000)
  @Around("execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postSaveData(..)) "
      + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postSubmitApplication(..)) "
      + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postCloseApplication(..)) "
      + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postCoordinatorAssignToTeamLead(..)) "
      + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postReworkApplication(..)) "
      + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postApplicationHandlingOfficer(..)) "
      + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postApplicationCoordinator(..)) "
      + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postCreateApplication(..))"
      + "|| execution(* com.msb.bpm.approval.appr.service.cms.impl.CmsIntegrationServiceImpl.v2PostDocuments(..))"
      + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationFeedbackServiceImpl.applicationFeedbackCustomer(..))"
      + "|| execution(* com.msb.bpm.approval.appr.service.cms.impl.CmsIntegrationServiceImpl.postChecklistByBpmApplicationId(..))"
      + "|| execution(* com.msb.bpm.approval.appr.service.common.CopyApplicationService.copyApplication(..))")
  public Object pushDataToGeneralInfo(ProceedingJoinPoint joinPoint) throws Throwable {
    Object response = joinPoint.proceed();
    Object[] parameters = joinPoint.getArgs();

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    List<String> bpmIds = new ArrayList<>();

    switch (method.getName()) {
      case "postCreateApplication":
        GetApplicationDTO getApplicationDTO = (GetApplicationDTO) response;
        bpmIds = Collections.singletonList(getApplicationDTO.getInitializeInfo().getApplication().getBpmId());
        pushToGeneralInfo(bpmIds);
        pushToBroadcast(bpmIds);
        pushToBroadcastV2(bpmIds);
        break;

      case "postApplicationCoordinator":
      case "postApplicationHandlingOfficer":
        PostAssignRequest request = (PostAssignRequest) parameters[0];
        bpmIds = request.getBpmIds();
        pushToGeneralInfo(bpmIds);
        pushToBroadcast(bpmIds);
        pushToBroadcastV2(bpmIds);
        break;

      case "postChecklistByBpmApplicationId":
        bpmIds = Collections.singletonList(parameters[1].toString());
        pushToGeneralInfo(bpmIds);
        pushToBroadcast(bpmIds);
        pushToBroadcastV2(bpmIds);
        break;

      case "v2PostDocuments":
        bpmIds = Collections.singletonList(parameters[1].toString());
        pushToGeneralInfo(bpmIds);

        Map<String, Object> metadata =  ((CmsBaseResponse)response).getMetadata();
        boolean isStartCamunda = (boolean) metadata.get("isStartCamunda");
        String currentLdpStatus = (String) metadata.get("currentLdpStatus");

        if (isStartCamunda || !LdpStatus.CUSTOMER_EDITED.getValue().equals(currentLdpStatus)) {
          pushToBroadcast(bpmIds);
          pushToBroadcastV2(bpmIds);
        }

        break;

      case "postSaveData":
        bpmIds = Collections.singletonList(parameters[0].toString());
        pushToGeneralInfo(bpmIds);
        break;

      case "copyApplication":
        bpmIds = Collections.singletonList(response.toString());
        pushToKafka(GENERAL_INFO.getValue(), bpmIds);
        pushToKafka(CMS_INFO.getValue(), bpmIds);
        pushToKafka(CMS_V2_INFO.getValue(), bpmIds);
        break;

      case "applicationFeedbackCustomer":
        PostApplicationFeedbackRequest postApplicationFeedbackRequest = (PostApplicationFeedbackRequest) parameters[0];
        String bpmId = postApplicationFeedbackRequest.getBpmId();
        bpmIds.add(bpmId);
        pushToKafka(GENERAL_INFO.getValue(), bpmIds);
        break;

      default:
        bpmIds = Collections.singletonList(parameters[0].toString());
        pushToGeneralInfo(bpmIds);
        pushToBroadcast(bpmIds);
        pushToBroadcastV2(bpmIds);
        break;
    }

    return response;
  }

  public void pushToGeneralInfo(List<String> bpmIds) {
    ConcurrentMap<String, Object> metadata = new ConcurrentHashMap<>();
    metadata.put(APPLICATION_BPM_IDS, bpmIds);
    new BeanProducer(metadata, generalInfoProducerStrategy).execute();
  }

  public void pushToBroadcast(List<String> bpmIds) {
    ConcurrentMap<String, Object> broadcastMetadata = new ConcurrentHashMap<>();
    broadcastMetadata.put(APPLICATION_BPM_IDS, bpmIds);
    new BeanProducer(broadcastMetadata, cmsInfoProducerStrategy).execute();
  }

  public void pushToBroadcastV2(List<String> bpmIds) {
    ConcurrentMap<String, Object> broadcastMetadata = new ConcurrentHashMap<>();
    broadcastMetadata.put(APPLICATION_BPM_IDS, bpmIds);
    new BeanProducer(broadcastMetadata, cmsV2InfoProducerStrategy).execute();
  }

  public void pushToKafka(String eventType, List<String> bpmIds) {
    applicationEventPublisher.publishEvent(new PushKafkaEvent(this, eventType, bpmIds));
  }

  /**
   * Gửi email
   *
   * @param joinPoint
   * @return
   * @throws Throwable
   */
    @Transactional(readOnly = true)
    @Around("execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postApplicationHandlingOfficer(..)) "
            + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postApplicationCoordinator(..)) "
            + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postCloseApplication(..)) "
            + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postCoordinatorAssignToTeamLead(..)) "
            + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postReworkApplication(..)) "
            + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationServiceImpl.postSubmitApplication(..)) "
            + "|| execution(* com.msb.bpm.approval.appr.service.application.impl.ApplicationFeedbackServiceImpl.applicationFeedbackCustomer(..)) "
            + "|| execution(* com.msb.bpm.approval.appr.service.cms.impl.CmsIntegrationServiceImpl.v2PostDocuments(..)) "
    )
    public Object pushEmail(ProceedingJoinPoint joinPoint) throws Throwable {
        Object response = joinPoint.proceed();

        Object[] parameters = joinPoint.getArgs();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        log.info("publishEmail with method={}, parameters={}", method.getName(),
                JsonUtil.convertObject2String(parameters, objectMapper));
        switch (method.getName()) {
            case "postApplicationHandlingOfficer":
                PostAssignRequest request = (PostAssignRequest) parameters[0];
                sendEmail(null, TEAM_LEAD_ASSIGN_APPROVER.name(), null, null, request.getReceptionUser(), request.getBpmIds());
                break;
            case "postApplicationCoordinator":
                PostAssignRequest requestCoordinator = (PostAssignRequest) parameters[0];
                sendEmail(null, TEAM_LEAD_ASSIGN_CONTACT.name(), null, null, requestCoordinator.getReceptionUser(), requestCoordinator.getBpmIds());
                break;
            case "postCloseApplication":
                String bpmId = (String) parameters[0];
                List<ApplicationApprovalHistoryDTO> lstHistoryApprove =
                        approvalHistoryService.getApplicationApprovalHistory(bpmId);
                Set<String> reasons = null;
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(lstHistoryApprove)) {
                    reasons = lstHistoryApprove.get(0).getReasons();
                }
                sendEmail(bpmId, EventCodeEmailType.CLOSE.name(), null, reasons, null, null);
                break;
            case "postCoordinatorAssignToTeamLead":
                sendEmail((String) parameters[0], EventCodeEmailType.APPROVER_ESCALATE.name(), null, null, null, null);
                addTeamLeadToRoom((String) parameters[0]);
                break;
            case "postReworkApplication":
                sendEmailRework((String) parameters[0]);
                break;
            case "postSubmitApplication":
                ApplicationEntity entityApp = (ApplicationEntity) response;
                sendEmailSubmit((String) parameters[0], entityApp.getProcessingStepCode());
                addUserToRoomChat(entityApp.getBpmId(), entityApp.getAssignee());
                break;
            case "applicationFeedbackCustomer":
                PostApplicationFeedbackRequest feedbackRequest = (PostApplicationFeedbackRequest) parameters[0];
                MessageResponse messageResponse = (MessageResponse) response;
                if(MessageCode.FEEDBACK_EDITING_SUCCESS.getCode().equalsIgnoreCase(messageResponse.getCode()) ||
                MessageCode.FEEDBACK_CONFIRM_SUCCESS.getCode().equalsIgnoreCase(messageResponse.getCode())) {
                  sendEmailFeedback(feedbackRequest.getBpmId(), EventCodeEmailType.LDP_NOTICE_FEEDBACK.name(),null, null);
                }
                break;
            case "v2PostDocuments":
                PostCmsV2DocumentsRequest documentsRequest = (PostCmsV2DocumentsRequest) parameters[0];
                String bpmIdRequest = (String) parameters[1];
                CmsBaseResponse cmsBaseResponse = (CmsBaseResponse) response;
                Map<String, Object> metadata = cmsBaseResponse.getMetadata();
                String currentLdpStatus = (String) metadata.get("currentLdpStatus");
                String feedbackReason = getFeedbackReason(currentLdpStatus,
                    documentsRequest.getConfirmStatus().name(), documentsRequest.getReason());
                if (ApplicationStatus.AS4003.getValue().equalsIgnoreCase(currentLdpStatus)) {
                  sendEmailFeedback(bpmIdRequest, EventCodeEmailType.LDP_SEND_CONFIRM_CUSTOMER.name(),
                      feedbackReason, documentsRequest.getConfirmStatus().name());
                }
                if (ApplicationStatus.AS4001.getValue().equalsIgnoreCase(currentLdpStatus)) {
                  sendEmailFeedback(bpmIdRequest, EventCodeEmailType.LDP_SEND_FEEDBACK_CUSTOMER.name(),
                      feedbackReason, documentsRequest.getConfirmStatus().name());
                }
                break;
            default:
                break;
        }

        return response;
    }

    private void sendEmailSubmit(String bpmId, String processingStepCode) {
        log.info("sendEmailSubmit with processingStepCode={}", processingStepCode);
        switch (ProcessingStep.get(processingStepCode)) {
            case APPROVE_PROPOSAL:
                sendEmail(bpmId, EventCodeEmailType.RM_SUBMIT.name(), null, null, null, null);
                break;
            case TL_COORDINATOR:
            case COORDINATOR:
            case APPROVE_PROFILE_1:
                sendEmail(bpmId, EventCodeEmailType.BM_ENDORSE.name(), null, null, null, null);
                break;
            case FLOW_COMPLETE:
                sendEmail(bpmId, EventCodeEmailType.APPROVER_APPROVE.name(), null, null, null, null);
                break;
            default:
                sendEmail(bpmId, EventCodeEmailType.APPROVER_ESCALATE.name(), null, null, null, null);
                break;
        }
    }

    private void sendEmailRework(String bpmId) {
        log.info("sendEmailRework START with bpmId={}", bpmId);
        // get received mail
        List<ApplicationApprovalHistoryDTO> lstHistoryApprove =
                approvalHistoryService.getApplicationApprovalHistory(bpmId);
        ProcessingRole processingRole = null;
        Set<String> reasons = null;
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(lstHistoryApprove)) {
            processingRole = ProcessingRole.valueOf(lstHistoryApprove.get(0).getFromUserRole());
            reasons = lstHistoryApprove.get(0).getReasons();
        }

        log.info("sendEmailRework with processingRole={}",
                Objects.nonNull(processingRole) ? processingRole.name() : null);
        if (Objects.nonNull(processingRole)) {
            switch (processingRole) {
                case PD_RB_BM:
                    sendEmail(bpmId, EventCodeEmailType.BM_RETURN.name(), null, reasons, null, null);
                    break;
                case PD_RB_CONTACT:
                    sendEmail(bpmId, EventCodeEmailType.CONTACT_RETURN.name(), null, reasons, null, null);
                    break;
                case PD_RB_CA1:
                case PD_RB_CA2:
                case PD_RB_CA3:
                case PD_RB_CC_CONTROL:
                case PD_RB_CC1:
                case PD_RB_CC2:
                case PD_RB_CC3:
                    sendEmail(bpmId, EventCodeEmailType.APPROVER_RETURN.name(), null, reasons, null, null);
                    break;
                default:
                    break;
            }
        }
    }

    public void sendEmail(String bpmId, String emailType, List<String> listFileNames,
                          Set<String> reasons, String assignee, List<String> bpmIds) {
        ConcurrentMap<String, Object> metadata = new ConcurrentHashMap<>();
        if (StringUtils.isNotBlank(bpmId)) {
            metadata.put(APPLICATION_BPM_ID, bpmId);
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(listFileNames)) {
            metadata.put("DOCUMENT_CODE", String.join(",", listFileNames));
        }
        if (StringUtils.isNotBlank(assignee)) {
            metadata.put(ASSIGNEE, assignee);
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(reasons)) {
            metadata.put(REASONS, reasons);
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(bpmIds)) {
            metadata.put(APPLICATION_BPM_IDS, bpmIds);
        }
        metadata.put(TOKEN, HeaderUtil.getToken());
        // Send email
        aop.sendEmail(emailType, metadata);
    }

  public void sendEmailFeedback(String bpmId, String evenCode, String reason, String confirmStatus) {
    log.info("sendEmailFeedback with evenCode {} and bpmId={}", evenCode, bpmId);
    ConcurrentMap<String, Object> metadata = new ConcurrentHashMap<>();
    if (StringUtils.isBlank(bpmId)) {
      bpmId = "";
    }
    if (StringUtils.isBlank(reason)) {
      reason = "";
    }
    if (StringUtils.isBlank(confirmStatus)) {
      confirmStatus = "";
    }
    metadata.put(APPLICATION_BPM_ID, bpmId);
    metadata.put(LDP_REASON, reason);
    metadata.put(LDP_CONFIRM_STATUS, confirmStatus);
    metadata.put(TOKEN, HeaderUtil.getToken());

    aop.sendEmail(evenCode, metadata);

  }

  public String getFeedbackReason(String currentLdpStatus, String confirmStatus, String reason) {
    String feedbackReason = "";
    if (StringUtils.isEmpty(reason)) {
      reason = "";
    }
    if (ApplicationStatus.AS4003.getValue().equalsIgnoreCase(currentLdpStatus)
    && LdpConfirmStatus.REJECT.name().equalsIgnoreCase(confirmStatus)) {
      feedbackReason = reason;
    }
    if (ApplicationStatus.AS4003.getValue().equalsIgnoreCase(currentLdpStatus)
    && LdpConfirmStatus.EXPIRED.name().equalsIgnoreCase(confirmStatus)) {
      feedbackReason = NoticeFeedback.NOTICE_FEEDBACK.getValue();
    }
    if (ApplicationStatus.AS4001.getValue().equalsIgnoreCase(currentLdpStatus)
    && StringUtils.isEmpty(reason)) {
      feedbackReason = "./.";
    }
    if (ApplicationStatus.AS4001.getValue().equalsIgnoreCase(currentLdpStatus)
    && StringUtils.isNotEmpty(reason)) {
      feedbackReason = reason;
    }
    return feedbackReason;
  }

  @AfterThrowing(value = "execution(* com.msb.bpm.approval.appr.service.application.impl.PostAssetInfoServiceImpl.execute(..))",
          throwing = "ex")
  public void updateAssetInfoDraftData(JoinPoint joinPoint, ApprovalException ex) throws Throwable {
    try {
      if(DomainCode.ASSET_INVALID_ERROR.getCode().equals(ex.getCode().getCode())) {
        Object[] args = joinPoint.getArgs();
        Object[] params = (Object[]) args[1];
        String bpmId = (String) params[0];

        PostAssetInfoRequest request = (PostAssetInfoRequest) args[0];
        persistApplicationDraft(bpmId, ASSET_INFO, UNFINISHED, request);
      }
    } catch (Exception exception) {
      log.error("afterThrowingAdvice error: {}", exception);
    }
  }

  private void addTeamLeadToRoom(String bpmId) {
    applicationRepository.findByBpmId(bpmId).ifPresent(applicationEntity -> {
      if (StringUtils.isNotEmpty(applicationEntity.getAssignee())) {
        addUserToRoomChat(applicationEntity.getBpmId(), applicationEntity.getAssignee());
      }
    });
  }

  private void addUserToRoomChat(String groupName, String assignee) {
    try {
      if (ObjectUtils.isNotEmpty(assignee)) {
        applicationEventPublisher.publishEvent(new AddUserToRoomEvent(this, groupName, assignee));
      }
    } catch (Exception ex) {
      log.error("addUserToRoomChat error: ", ex);
    }
  }

  @Order(1001)
  @AfterReturning(value = "execution(* com.msb.bpm.approval.appr.service.intergated.impl.BaseIntegrationServiceImpl.createOrRetryCreditCard(..))", returning = "applicationHistoricIntegration")
  public void sendKafkaWay4(JoinPoint joinPoint, ApplicationHistoricIntegration applicationHistoricIntegration) {
    log.info("START sendKafkaWay4 with data: {}", JsonUtil.convertObject2String(applicationHistoricIntegration, objectMapper));
    String bpmId = applicationHistoricIntegration.getBpmId();
    if (Objects.nonNull(bpmId)) {
      try {
        log.info("START sending message way4 to kafka with id: {}", bpmId);
        pushToBroadcastV2(Collections.singletonList(bpmId));
        log.info("FINISH sending message way4 to kafka with id: {}", bpmId);
      } catch (Exception exception) {
        log.error("sendKafkaWay4 error: ", exception);
      }
    } else log.info("message way4 to kafka has NULL application bpm id");
  }

}
