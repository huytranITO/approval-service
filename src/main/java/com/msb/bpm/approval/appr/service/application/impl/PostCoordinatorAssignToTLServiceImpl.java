package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_ASSIGN_TO_TL;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.isComplete;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_CONTACT_LEAD;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.PROCESSING_STEP;
import static com.msb.bpm.approval.appr.exception.DomainCode.APPLICATION_PREVIOUSLY_CLOSED;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_USER_RECEPTION;
import static com.msb.bpm.approval.appr.exception.DomainCode.USER_DONT_HAVE_PERMISSION;
import static java.util.Comparator.comparing;

import com.msb.bpm.approval.appr.enums.camunda.ButtonEventCode;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.mapper.DecisionRuleMapping;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.verify.VerifyService;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.camunda.CamundaService;
import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 8/5/2023, Monday
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class PostCoordinatorAssignToTLServiceImpl extends AbstractBaseService implements
    BaseService<ApplicationEntity, String> {

  private final ApplicationRepository applicationRepository;
  private final DecisionRuleIntegrateService decisionRuleIntegrateService;
  private final CamundaService camundaService;
  private final VerifyService verifyService;
  private final ConfigurationServiceCache configurationServiceCache;

  @Override
  public String getType() {
    return POST_ASSIGN_TO_TL;
  }

  @Override
  @Transactional
  public ApplicationEntity execute(String bpmId, Object... obj) {
    log.info("PostCoordinatorAssignToTLServiceImpl.execute() start with bpmId : {}", bpmId);

    return Optional.of(applicationRepository.findByBpmId(bpmId))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(applicationEntity -> {

          if (!SecurityContextUtil.getCurrentUser()
              .equalsIgnoreCase(applicationEntity.getAssignee())) {
            throw new ApprovalException(USER_DONT_HAVE_PERMISSION);
          }

          if (isComplete(applicationEntity.getStatus())) {
            throw new ApprovalException(APPLICATION_PREVIOUSLY_CLOSED);
          }

          // Lấy lại thông tin user Role Team lead đã chuyển hồ sơ gần nhất
          String userReception = getUserReceptionContactLead(
              applicationEntity.getHistoryApprovals());

          // Verify thông tin CB được gán hồ sơ
          verifyService.verifyUserReception(userReception, null);

          // Get rule checking workflow
          RuleResponse ruleResponse = (RuleResponse) decisionRuleIntegrateService.getDecisionRule(
              applicationEntity.getId(),
              RuleCode.R003, DecisionRuleMapping.INSTANCE.toTransitionCondition(applicationEntity)
                  .withCredits(buildCredits(applicationEntity.getCredits()))
                  .withEventCode(ButtonEventCode.REASSIGN_TEAM_LEAD.getCode()));

          List<CategoryDataResponse> dataResponses = configurationServiceCache.getCategoryDataByCode(
              PROCESSING_STEP);

          Map<String, VariableValueDto> returnVar = camundaService.completeTaskWithReturnVariables(
              applicationEntity, ruleResponse.getRuleDataItem().getData().getStepCode());

          applicationRepository.saveData(applicationEntity, ruleResponse, returnVar, userReception,
              dataResponses);

          log.info(
              "PostCoordinatorAssignToTLServiceImpl.execute() end with bpmId : {} , user team lead reception : {} camunda task response : {}",
              bpmId, userReception, returnVar);

          return applicationEntity;

        })
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));
  }

  private String getUserReceptionContactLead(
      Set<ApplicationHistoryApprovalEntity> historyApprovalEntities) {
    historyApprovalEntities = historyApprovalEntities
        .stream()
        .filter(filterHistory -> PD_RB_CONTACT_LEAD.equals(filterHistory.getFromUserRole()))
        .sorted(comparing(ApplicationHistoryApprovalEntity::getExecutedAt).reversed())
        .collect(Collectors.toCollection(LinkedHashSet::new));

    if (CollectionUtils.isNotEmpty(historyApprovalEntities)) {
      return historyApprovalEntities.stream()
          .map(ApplicationHistoryApprovalEntity::getCreatedBy)
          .findFirst()
          .orElseThrow(() -> new ApprovalException(NOT_FOUND_USER_RECEPTION));
    }

    throw new ApprovalException(NOT_FOUND_USER_RECEPTION);
  }
}
