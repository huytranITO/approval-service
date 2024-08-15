package com.msb.bpm.approval.appr.service.intergated.impl;

import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.STOP_TRANSITION;
import static com.msb.bpm.approval.appr.exception.DomainCode.DONT_ALLOW_STEP_TRANSITIONS;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_RULE_CONDITION_TRANSITIONS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.authority.AuthorityClient;
import com.msb.bpm.approval.appr.client.rule.RuleClient;
import com.msb.bpm.approval.appr.enums.authority.AuthorityClass;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.mapper.DecisionRuleMapping;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDetailDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.response.RuleTemplateResponseDTO;
import com.msb.bpm.approval.appr.model.dto.rule.TransitionConditionDTO;
import com.msb.bpm.approval.appr.model.entity.RuleVersionMappingEntity;
import com.msb.bpm.approval.appr.model.request.BodyRequest;
import com.msb.bpm.approval.appr.model.request.authority.AuthorityCheckerRequest;
import com.msb.bpm.approval.appr.model.request.authority.UserAuthorityRequest;
import com.msb.bpm.approval.appr.model.request.rule.RuleRequest;
import com.msb.bpm.approval.appr.model.response.authority.AuthorityInfoResponse;
import com.msb.bpm.approval.appr.model.response.authority.UserAuthorityResponse;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.RuleVersionMappingRepository;
import com.msb.bpm.approval.appr.service.intergated.AuthorityService;
import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 29/5/2023, Monday
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionRuleIntegrateServiceImpl implements DecisionRuleIntegrateService {

  private final RuleClient ruleClient;
  private final AuthorityClient authorityClient;
  private final ObjectMapper objectMapper;
  private final RuleVersionMappingRepository ruleVersionMappingRepository;
  private final ApplicationRepository applicationRepository;
  private final AuthorityService authorityService;

  @Override
  @Transactional
  public Object getDecisionRule(Long applicationId, RuleCode ruleCode, Object payloadData) {
    if (ruleCode == null) {
      throw new ApprovalException(DomainCode.RULE_CODE_MUST_NOT_NULL);
    }

    List<RuleVersionMappingEntity> ruleVersionMapping =
        ruleVersionMappingRepository
            .findByApplicationIdAndRuleCode(applicationId, ruleCode)
            .orElse(null);

    Object decisionRuleResponse;
    if (CollectionUtils.isEmpty(ruleVersionMapping)) {
      log.info(
          "Rule {} has never been used in application {}, get rule with latest version...",
          ruleCode,
          applicationId);
      decisionRuleResponse = getDecisionRule(ruleCode, null, payloadData);
      Integer ruleVersion = getRuleVersion(decisionRuleResponse);
      if (!Objects.isNull(ruleVersion)) {
        saveRuleVersionMapping(getRuleVersion(decisionRuleResponse), ruleCode, applicationId);
      }
    } else {
      log.info(
          "Rule {} was used before in application {}, get rule with version {}",
          ruleCode,
          applicationId,
          ruleVersionMapping.get(0).getRuleVersion());
      decisionRuleResponse =
          getDecisionRule(ruleCode, ruleVersionMapping.get(0).getRuleVersion(), payloadData);
    }

    log.info("Rule {} response : {}", ruleCode,
        JsonUtil.convertObject2String(decisionRuleResponse, objectMapper));

    return decisionRuleResponse;
  }

  public Object getDecisionRule(RuleCode ruleCode, Integer ruleVersion, Object payloadData) {
    switch (ruleCode) {
      case R001:
        // Rule checklist
        return null;
      case R003:
        // Rule workflow
        return getTransitionRule(ruleCode, ruleVersion, (TransitionConditionDTO) payloadData);
      case R002:
        // Rule check authority
        return authorityService.checkAuthority(
            (AuthorityCheckerRequest) payloadData, HeaderUtil.getToken(), ruleVersion);
      case R004:
        // Rule form template
        return searchByCode(
            payloadData, ruleCode, ruleVersion, null, RuleTemplateResponseDTO.class);
      default:
        return null;
    }
  }

  /**
   * Get Rule Workflow
   *
   * @param ruleCode RuleCode
   * @param ruleVersion Integer
   * @param transitionCondition TransitionConditionDTO
   * @return RuleResponse
   */
  private RuleResponse getTransitionRule(
      RuleCode ruleCode, Integer ruleVersion, TransitionConditionDTO transitionCondition) {
    if (Objects.isNull(transitionCondition)) {
      throw new ApprovalException(NOT_FOUND_RULE_CONDITION_TRANSITIONS);
    }

    UserAuthorityResponse userAuthorityResponse =
        authorityClient.searchAuthority(
            new UserAuthorityRequest(SecurityContextUtil.getCurrentUser(),
                AuthorityClass.valueOf(transitionCondition.getPriorityAuthority())),
            HeaderUtil.getToken());

    Integer userAuthorityLevel = 0;
    List<String> userAuthorityCode = null;

    if (userAuthorityResponse != null && CollectionUtils.isNotEmpty(
        userAuthorityResponse.getData())) {
      userAuthorityLevel =
          userAuthorityResponse.getData().size() == 1 ? userAuthorityResponse.getData().get(0)
              .getPriority() : null;
      userAuthorityCode = userAuthorityResponse.getData().stream()
          .map(AuthorityInfoResponse::getCode)
          .collect(Collectors.toList());
    }

    RuleRequest request =
        DecisionRuleMapping.INSTANCE
            .toRuleRequest(transitionCondition)
            .withUserAuthorityLevel(userAuthorityLevel)
            .withUserAuthorityCode(userAuthorityCode);

    log.info("Rule {} request : {}", ruleCode, JsonUtil.convertObject2String(request, objectMapper));

    RuleResponse response =
        searchByCode(
            request, ruleCode, ruleVersion, transitionCondition.getBpmId(), RuleResponse.class);

    if (Objects.nonNull(response)
        && Objects.nonNull(response.getRuleDataItem())
        && Objects.nonNull(response.getRuleDataItem().getData())) {

      if (STOP_TRANSITION.getCode()
          .equalsIgnoreCase(response.getRuleDataItem().getData().getStepCode())) {
        throw new ApprovalException(DONT_ALLOW_STEP_TRANSITIONS);
      }

      return response;
    }

    throw new ApprovalException(NOT_FOUND_RULE_CONDITION_TRANSITIONS);
  }

  private <T, D> T searchByCode(
      D request, RuleCode ruleCode, Integer ruleVersion, String applicationBpmId, Class<T> tClass) {
    Object obj =
        ruleClient.searchByCode(
            BodyRequest.builder()
                .data(request)
                .code(ruleCode.name())
                .version(ruleVersion)
                .applicationBpmId(applicationBpmId)
                .build(),
            HeaderUtil.getToken());

    return objectMapper.convertValue(obj, tClass);
  }

  private Integer getRuleVersion(Object decisionRuleResponse) {
    if (decisionRuleResponse instanceof AuthorityDetailDTO) {
      return ((AuthorityDetailDTO) decisionRuleResponse).getVersion();
    }
    if (decisionRuleResponse instanceof RuleResponse) {
      return ((RuleResponse) decisionRuleResponse).getRuleDataItem().getVersion();
    }
    if (decisionRuleResponse instanceof RuleTemplateResponseDTO) {
      return ((RuleTemplateResponseDTO) decisionRuleResponse).getData().getVersion();
    }
    return null;
  }

  @Transactional
  public void saveRuleVersionMapping(Integer ruleVersion, RuleCode ruleCode, Long applicationId) {
    ruleVersionMappingRepository.save(
        new RuleVersionMappingEntity()
            .withRuleVersion(ruleVersion)
            .withRuleCode(ruleCode)
            .withApplication(applicationRepository.getReferenceById(applicationId)));
    log.info("Application {} saved rule {} with version {}", applicationId, ruleCode, ruleVersion);
  }
}
