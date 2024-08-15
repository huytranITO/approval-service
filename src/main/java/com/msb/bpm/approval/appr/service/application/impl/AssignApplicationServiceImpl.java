package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.ASSIGN_APPLICATION;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.isAssignUserReception;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.isComplete;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.PROCESSING_STEP;
import static com.msb.bpm.approval.appr.exception.DomainCode.APPLICATION_PREVIOUSLY_CLOSED;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.exception.DomainCode.SUCCESS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.chat.events.AddUserToListRoomEvent;
import com.msb.bpm.approval.appr.enums.camunda.CamundaAction;
import com.msb.bpm.approval.appr.enums.email.EventCodeEmailType;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.mapper.DecisionRuleMapping;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.flow.PostAssignRequest;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.verify.VerifyService;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.camunda.CamundaService;
import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
import com.msb.bpm.approval.appr.util.CamundaUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 1/6/2023, Thursday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class AssignApplicationServiceImpl extends AbstractBaseService implements
    BaseService<Object, PostAssignRequest> {

  private final ApplicationRepository applicationRepository;
  private final DecisionRuleIntegrateService decisionRuleIntegrateService;
  private final CamundaService camundaService;
  private final VerifyService verifyService;
  private final ObjectMapper objectMapper;
  private final ConfigurationServiceCache configurationServiceCache;

  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public String getType() {
    return ASSIGN_APPLICATION;
  }

  @Override
  @Transactional
  public Object execute(PostAssignRequest postAssignRequest, Object... obj) {

    log.info("AssignApplicationServiceImpl.execute() start with request : {}",
        JsonUtil.convertObject2String(postAssignRequest, objectMapper));

    // Verify thông tin CB được gán hồ sơ
    verifyService.verifyUserReception(postAssignRequest.getReceptionUser(), null);

    CamundaAction action = (CamundaAction) obj[0];

    EventCodeEmailType eventCodeEmail = (EventCodeEmailType) obj[1];

    List<String> lstRoomNames = new ArrayList<>();
    postAssignRequest.getBpmIds().forEach(
        bpmId -> {
          ((AssignApplicationServiceImpl) AopContext.currentProxy()).setAssignee(action,
                  bpmId, postAssignRequest.getReceptionUser(), eventCodeEmail);
          lstRoomNames.add(bpmId);
        });

    log.info("AssignApplicationServiceImpl.execute() end with request : {}",
        JsonUtil.convertObject2String(postAssignRequest, objectMapper));

    applicationEventPublisher.publishEvent(new AddUserToListRoomEvent(this, lstRoomNames, postAssignRequest.getReceptionUser()));
    return SUCCESS.getMessage();
  }

  @Transactional
  public ApplicationEntity setAssignee(CamundaAction action, String bpmId, String receptionUser,
      EventCodeEmailType eventCodeEmail) {

    ApplicationEntity applicationEntity = applicationRepository.findByBpmId(bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));

    if (isComplete(applicationEntity.getStatus())) {
      throw new ApprovalException(APPLICATION_PREVIOUSLY_CLOSED);
    }

    // Hồ sơ chưa được assign lần nào
    if (isAssignUserReception(applicationEntity.getStatus())) {
      // Get rule checking workflow
      RuleResponse ruleResponse = (RuleResponse) decisionRuleIntegrateService.getDecisionRule(
          applicationEntity.getId(), RuleCode.R003,
          DecisionRuleMapping.INSTANCE.toTransitionCondition(applicationEntity)
              .withCredits(buildCredits(applicationEntity.getCredits()))
              .withEventCode(CamundaUtil.getButtonEventCode(action.getValue()).getCode()));

      List<CategoryDataResponse> dataResponses = configurationServiceCache.getCategoryDataByCode(
          PROCESSING_STEP);

      Map<String, VariableValueDto> returnVar = camundaService.completeTaskWithReturnVariables(
          applicationEntity, ruleResponse.getRuleDataItem().getData().getStepCode());

      applicationRepository.saveData(applicationEntity, ruleResponse, returnVar, receptionUser,
          dataResponses);

      log.info("Assigned bpmId {} for user {}, camunda task response : {}", bpmId, receptionUser,
          returnVar);

    } else {
      applicationEntity.setReceptionAt(LocalDateTime.now());
      applicationEntity.setAssignee(receptionUser);
      applicationRepository.save(applicationEntity);

      log.info("Assigned bpmId {} for user {}", bpmId, receptionUser);
    }

    return applicationEntity;
  }
}
