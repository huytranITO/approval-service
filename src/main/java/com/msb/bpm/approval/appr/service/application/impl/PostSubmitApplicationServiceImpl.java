package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.CBT_RUN_PROCESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.CBT_RUN_PROCESS_STATUS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_COMPLETE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_SUBMIT_APP;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.BlockKey.FORM_TEMPLATE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DOCUMENTS_INFO;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.isComplete;
import static com.msb.bpm.approval.appr.enums.application.AppraisalContent.OTHER_ADDITIONAL;
import static com.msb.bpm.approval.appr.enums.application.AppraisalContent.SPECIAL_RISK;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.PROCESSING_STEP;
import static com.msb.bpm.approval.appr.exception.DomainCode.APPLICATION_PREVIOUSLY_CLOSED;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.exception.DomainCode.USER_DONT_HAVE_PERMISSION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.chat.events.CloseGroupEvent;
import com.msb.bpm.approval.appr.enums.camunda.ButtonEventCode;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.factory.CustomerFactory;
import com.msb.bpm.approval.appr.mapper.ApplicationLimitCreditMapper;
import com.msb.bpm.approval.appr.mapper.DecisionRuleMapping;
import com.msb.bpm.approval.appr.model.dto.ApplicationAppraisalContentDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationLimitCreditDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostCompleteRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostSubmitRequest;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import com.msb.bpm.approval.appr.repository.ApParamRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.camunda.CamundaService;
import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
import com.msb.bpm.approval.appr.service.verify.VerifyService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 8/5/2023, Monday
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostSubmitApplicationServiceImpl extends AbstractBaseService
    implements BaseService<ApplicationEntity, PostBaseRequest> {

  private final ApplicationRepository applicationRepository;
  private final VerifyService verifyService;
  private final DecisionRuleIntegrateService decisionRuleIntegrateService;
  private final CamundaService camundaService;
  private final ObjectMapper objectMapper;
  private final ConfigurationServiceCache configurationServiceCache;
  private final ApParamRepository apParamRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public String getType() {
    return POST_SUBMIT_APP;
  }

  @Override
  @Transactional
  public ApplicationEntity execute(PostBaseRequest request, Object... args) {
    log.info("PostSubmitApplicationServiceImpl.execute start with bpmId : {} , request : {} =)",
        args[0], JsonUtil.convertObject2String(request, objectMapper));

    return Optional.of(applicationRepository.findByBpmId((String) args[0]))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(
            applicationEntity -> {

              if (!SecurityContextUtil.getCurrentUser()
                  .equalsIgnoreCase(applicationEntity.getAssignee())) {
                throw new ApprovalException(USER_DONT_HAVE_PERMISSION);
              }

              if (isComplete(applicationEntity.getStatus())) {
                throw new ApprovalException(APPLICATION_PREVIOUSLY_CLOSED);
              }

              // Verify danh mục hồ sơ checklist
              verifyService.verifyDocumentsInfo(applicationEntity.getBpmId(), true);

              // Verify thông tin hồ sơ
              // - Kiểm tra tích xanh của các tab hồ sơ
              // - Kiểm tra lại thông tin hạn mức tín dụng
              // - Kiểm tra lại thẩm quyền phê duyệt khoản vay vs các khoản vay hiện tại của hồ sơ
              verifyApplicationData(applicationEntity);

              // Verify thông tin hồ sơ nếu hoàn thành phê duyệt
              // - Kiểm tra OTP
              // - Kiểm tra lại thẩm quyền phê duyệt hồ sơ của user hiện tại
              verifyIfCompleteApplication(request, applicationEntity.getBpmId());

              // Get rule for workflow
              RuleResponse ruleResponse =
                  (RuleResponse)
                      decisionRuleIntegrateService.getDecisionRule(
                          applicationEntity.getId(),
                          RuleCode.R003,
                          DecisionRuleMapping.INSTANCE
                              .toTransitionCondition(applicationEntity)
                              .withCredits(buildCredits(applicationEntity.getCredits()))
                              .withEventCode(ButtonEventCode.SUCCESS.getCode()));

              verifyUserReception(request, ruleResponse);

              List<CategoryDataResponse> dataResponses = configurationServiceCache.getCategoryDataByCode(
                  PROCESSING_STEP);

              String nextStepCode = ruleResponse.getRuleDataItem().getData().getStepCode();

              Map<String, VariableValueDto> returnVar =
                  camundaService.completeTaskWithReturnVariables(applicationEntity, nextStepCode);

              String receptionUser =
                  POST_COMPLETE.equalsIgnoreCase(request.getType())
                      ? applicationEntity.getAssignee()
                      : ((PostSubmitRequest) request).getReceptionUser();

              // Xóa biểu mẫu/tờ trình đã generate tại thời điểm hiện tại sau khi trình hồ sơ
              applicationEntity.getExtraDatas()
                  .removeIf(extraData -> DOCUMENTS_INFO.equalsIgnoreCase(extraData.getCategory())
                      && FORM_TEMPLATE.equalsIgnoreCase(extraData.getKey()));

              applicationRepository.saveData(
                  applicationEntity, ruleResponse, returnVar, receptionUser, dataResponses);

              log.info(
                  "PostSubmitApplicationServiceImpl.execute() end with bpmId : {} , camunda task response : {}",
                  applicationEntity.getBpmId(),
                  JsonUtil.convertObject2String(returnVar, objectMapper));

                if (isComplete(applicationEntity.getStatus())) {
                    log.info("START SmartChat CloseGroup Event with applicationId={}",
                            applicationEntity.getBpmId());
                    applicationEventPublisher.publishEvent(new CloseGroupEvent(this, applicationEntity.getBpmId()));
                }

              return applicationEntity;
            })
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));
  }
  private void verifyUserReception(PostBaseRequest request, RuleResponse transitionRule) {
    if (POST_SUBMIT_APP.equalsIgnoreCase(request.getType())) {
      verifyService.verifyUserReception(((PostSubmitRequest) request).getReceptionUser(),
          transitionRule);
    }
  }

    private void verifyIfCompleteApplication(PostBaseRequest request, String bpmId) {
        if (POST_COMPLETE.equalsIgnoreCase(request.getType())) {
            // Verify OTP khi user du tham quyen phe duyet ho so
            verifyService.verifyOTP((PostCompleteRequest) request);

            // Check CIFI
            apParamRepository.findByCodeAndType(CBT_RUN_PROCESS_STATUS, CBT_RUN_PROCESS)
                    .ifPresent(conf -> verifyService.verifyCIFIStatus(bpmId));
        }
    }

  @Transactional(readOnly = true)
  public void verifyApplicationData(ApplicationEntity applicationEntity) {
    // Verify rmCommitStatus
    verifyService.verifyRmCommitStatus(applicationEntity);

      // Verify oprisk collateral
    verifyService.verifyOpRiskCollateral(applicationEntity);

    // Verify account number info
    verifyService.verifyAccountNumber(applicationEntity);

    // Verify application data info
    verifyService.verifyApplicationData(applicationEntity.getBpmId());

    // Verify CIC data
    verifyService.verifyCicWhenSubmit(applicationEntity);

    Set<ApplicationLimitCreditDTO> limitCreditDTOs = ApplicationLimitCreditMapper.INSTANCE.toLimitCredits(
        applicationEntity.getLimitCredits());
    Map<String, Set<ApplicationAppraisalContentDTO>> appraisalContentMap = buildAppraisalContent(
        applicationEntity.getAppraisalContents());

    // Verify limit credit
    verifyService.verifyLimitCredit(limitCreditDTOs, buildCredits(applicationEntity.getCredits()));

    // Verify authority
    CustomerEntity customerEntity = applicationEntity.getCustomer();
    verifyService.verifyApplicationAuthority(
        DecisionRuleMapping.INSTANCE.toAuthorityRequestMap(applicationEntity,
                CustomerFactory.getCustomer(customerEntity.getCustomerType()).build(customerEntity))
            .withLimitCredits(limitCreditDTOs)
            .withSpecialRiskContents(appraisalContentMap.get(SPECIAL_RISK.name()))
            .withAdditionalContents(appraisalContentMap.get(OTHER_ADDITIONAL.name())));
    // Kiểm tra thông tin tab tài sản đảm bảo
    verifyService.verifyAssetInfoStatus(applicationEntity.getBpmId());
  }
}
