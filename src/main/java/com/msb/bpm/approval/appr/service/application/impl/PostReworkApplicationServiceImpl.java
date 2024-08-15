package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_REWORK_APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.BlockKey.FORM_TEMPLATE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DOCUMENTS_INFO;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.isComplete;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.PROCESSING_STEP;
import static com.msb.bpm.approval.appr.exception.DomainCode.APPLICATION_PREVIOUSLY_CLOSED;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.exception.DomainCode.USER_DONT_HAVE_PERMISSION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.enums.application.RmCommitStatus;
import com.msb.bpm.approval.appr.enums.camunda.ButtonEventCode;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.mapper.DecisionRuleMapping;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.flow.PostReworkApplicationRequest;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.verify.VerifyService;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.camunda.CamundaService;
import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 8/5/2023, Monday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class PostReworkApplicationServiceImpl extends AbstractBaseService implements
    BaseService<ApplicationEntity, PostReworkApplicationRequest> {

  private final ApplicationRepository applicationRepository;
  private final DecisionRuleIntegrateService decisionRuleIntegrateService;
  private final CamundaService camundaService;
  private final VerifyService verifyService;
  private final ObjectMapper objectMapper;
  private final ConfigurationServiceCache configurationServiceCache;

  @Override
  public String getType() {
    return POST_REWORK_APPLICATION;
  }

  @Override
  @Transactional
  public ApplicationEntity execute(PostReworkApplicationRequest postReworkApplicationRequest,
      Object... obj) {
    log.info("PostReworkApplicationServiceImpl.execute() start with bpmId : {} , request : {}",
        obj[0],
        JsonUtil.convertObject2String(postReworkApplicationRequest, objectMapper));

    return Optional.of(applicationRepository.findByBpmId((String) obj[0]))
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

          // Verify thông tin CB được gán hồ sơ
          verifyService.verifyUserReception(postReworkApplicationRequest.getReceptionUser(), null);

          // Get checking rule workflow
          RuleResponse ruleResponse = (RuleResponse) decisionRuleIntegrateService.getDecisionRule(
              applicationEntity.getId(),
              RuleCode.R003, DecisionRuleMapping.INSTANCE.toTransitionCondition(applicationEntity)
                  .withCredits(buildCredits(applicationEntity.getCredits()))
                  .withEventCode(ButtonEventCode.REWORK.getCode()));

          List<CategoryDataResponse> dataResponses = configurationServiceCache.getCategoryDataByCode(
              PROCESSING_STEP);

          Map<String, VariableValueDto> returnVar = camundaService.completeTaskWithReturnVariables(
              applicationEntity, ruleResponse.getRuleDataItem().getData().getStepCode());

          setRmCommitStatus(ruleResponse, applicationEntity);

          // Xóa biểu mẫu/tờ trình đã generate tại thời điểm hiện tại sau khi trả hồ sơ
          applicationEntity.getExtraDatas()
              .removeIf(extraData -> DOCUMENTS_INFO.equalsIgnoreCase(extraData.getCategory())
                  && FORM_TEMPLATE.equalsIgnoreCase(extraData.getKey()));

          applicationRepository.saveData(applicationEntity, ruleResponse, returnVar,
              postReworkApplicationRequest.getReceptionUser(), dataResponses);

          log.info(
              "PostReworkApplicationServiceImpl.execute() end with bpmId : {} , camunda task response : {}",
              applicationEntity.getBpmId(), returnVar);

          return applicationEntity;

        })
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));
  }

  private void setRmCommitStatus(RuleResponse ruleResponse, ApplicationEntity applicationEntity) {
    String nextRole = ruleResponse.getRuleDataItem().getData().getNextRole();
    if (StringUtils.isNotBlank(nextRole) && PD_RB_RM.name().equalsIgnoreCase(nextRole)) {
      applicationEntity.setRmCommitStatus(RmCommitStatus.NO_COMMIT.isStatus());
    }
  }
}
