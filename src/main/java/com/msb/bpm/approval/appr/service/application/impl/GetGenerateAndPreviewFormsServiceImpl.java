package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.CBT_RUN_PROCESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.CBT_RUN_PROCESS_STATUS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.GET_PREVIEW_FORM;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.BlockKey.FORM_TEMPLATE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DOCUMENTS_INFO;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_BM;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.exception.DomainCode.USER_DONT_HAVE_PERMISSION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.enums.application.GeneratorStatusEnums;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.application.ProcessingStep;
import com.msb.bpm.approval.appr.enums.camunda.ButtonEventCode;
import com.msb.bpm.approval.appr.enums.common.PhaseCode;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.mapper.DecisionRuleMapping;
import com.msb.bpm.approval.appr.model.dto.application.PopupControlDTO;
import com.msb.bpm.approval.appr.model.dto.application.PreviewFormDTO;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDetailDTO;
import com.msb.bpm.approval.appr.model.dto.authority.UserReceptionDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateRuleDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.response.ChecklistFileMessageDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.response.RuleTemplateResponseDTO;
import com.msb.bpm.approval.appr.model.dto.rule.TransitionConditionDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import com.msb.bpm.approval.appr.repository.ApParamRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.verify.VerifyService;
import com.msb.bpm.approval.appr.service.formtemplate.FormTemplateService;
import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 20/6/2023, Tuesday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class GetGenerateAndPreviewFormsServiceImpl extends AbstractBaseService implements
    BaseService<PreviewFormDTO, String> {

  private final ApplicationRepository applicationRepository;
  private final PostSubmitApplicationServiceImpl postSubmitApplicationService;
  private final DecisionRuleIntegrateService decisionRuleIntegrateService;
  private final FormTemplateService formTemplateService;
  private final VerifyService verifyService;
  private final ObjectMapper objectMapper;
  private final ApParamRepository apParamRepository;

  @Override
  public String getType() {
    return GET_PREVIEW_FORM;
  }

  @Override
  @Transactional
  public PreviewFormDTO execute(String bpmId, Object... args) {
    log.info(
        "GetGenerateAndPreviewFormsServiceImpl.execute start with bpmId : {}", bpmId);

    return Optional.of(applicationRepository.findByBpmId(bpmId))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(applicationEntity -> {

          if (!SecurityContextUtil.getCurrentUser()
              .equalsIgnoreCase(applicationEntity.getAssignee())) {
            throw new ApprovalException(USER_DONT_HAVE_PERMISSION);
          }

          // Verify thông tin hồ sơ
          // - Kiểm tra tích xanh của các tab hồ sơ
          // - Kiểm tra lại thông tin hạn mức tín dụng
          // - Kiểm tra lại thẩm quyền phê duyệt khoản vay vs các khoản vay hiện tại của hồ sơ
          postSubmitApplicationService.verifyApplicationData(applicationEntity);

          log.info("Application bpmId : {} has generator status is : {}", bpmId,
              applicationEntity.getGeneratorStatus());

          // Verify danh mục hồ sơ checklist
          verifyService.verifyDocumentsInfo(applicationEntity.getBpmId(),
              !GeneratorStatusEnums.DEFAULT.getValue()
                  .equalsIgnoreCase(applicationEntity.getGeneratorStatus()));

          // Gọi rule workflow để xác định next step
          TransitionConditionDTO transitionCondition = DecisionRuleMapping.INSTANCE.toTransitionCondition(
                  applicationEntity).withCredits(buildCredits(applicationEntity.getCredits()))
              .withEventCode(ButtonEventCode.SUCCESS.getCode());
          RuleResponse ruleResponse = (RuleResponse) decisionRuleIntegrateService.getDecisionRule(
              applicationEntity.getId(), RuleCode.R003, transitionCondition);

          PreviewFormDTO response = new PreviewFormDTO();

          // Nếu trạng thái generate form generatorStatus = 00 (chưa/chờ generate form)
          // Thì thực hiện gọi rule template để xác định phase hiện tại có cần generate template hay không
          if (StringUtils.isBlank(applicationEntity.getGeneratorStatus())
              || GeneratorStatusEnums.DEFAULT.getValue().equalsIgnoreCase(applicationEntity.getGeneratorStatus())) {

            FormTemplateRuleDTO ruleFormRequest =
                FormTemplateRuleDTO.builder()
                    .segmentType(applicationEntity.getSegment())
                    .submissionPurpose(applicationEntity.getSubmissionPurpose())
                    .currentStep(applicationEntity.getProcessingStepCode())
                    .nextStatus(ruleResponse.getRuleDataItem().getData().getNextTask())
                    .customerGroup(applicationEntity.getCustomer().getIndividualCustomer().getSubject())
                    .authorityLevel(applicationEntity.getPriorityAuthority())
                    .build();

            // Gọi rule form template
            RuleTemplateResponseDTO ruleTemplateResponse =
                (RuleTemplateResponseDTO)
                    decisionRuleIntegrateService.getDecisionRule(
                        applicationEntity.getId(), RuleCode.R004, ruleFormRequest);

            if (ruleTemplateResponse != null
                && ruleTemplateResponse.getData() != null
                && CollectionUtils.isNotEmpty(ruleTemplateResponse.getData().getData())) {

              // Generate biên bản phê duyệt/tờ trình
              formTemplateService.generateFormTemplate(applicationEntity,
                  PhaseCode.NRM_ALL_S.name(),
                  ruleTemplateResponse,
                  applicationEntity.getSubmissionPurpose());

              // Set trạng thái generatorStatus = 01 (Đang generate form)
              applicationEntity.setGeneratorStatus(GeneratorStatusEnums.PROCESSING.getValue());

              response.setGeneratorStatus(applicationEntity.getGeneratorStatus());

              log.info(
                  "GetGenerateAndPreviewFormsServiceImpl.execute end with bpmId : {}, response : {}",
                  bpmId, JsonUtil.convertObject2String(response, objectMapper));

              return response;
            }
          }

          Set<ChecklistFileMessageDTO> listFile = buildExtraData(applicationEntity.getExtraDatas(),
              new ImmutablePair<>(DOCUMENTS_INFO, FORM_TEMPLATE), ChecklistFileMessageDTO.class);

          if (GeneratorStatusEnums.DONE.getValue().equals(applicationEntity.getGeneratorStatus())
              && CollectionUtils.isNotEmpty(listFile)) {
            response.setFiles(new ArrayList<>(listFile));
            response.setGeneratorStatus(GeneratorStatusEnums.DONE.getValue());
          } else {
            response.setGeneratorStatus(applicationEntity.getGeneratorStatus());
          }

          response.setPopupControl(new PopupControlDTO());

          String nextStepCode = ruleResponse.getRuleDataItem().getData().getStepCode();

          switch (ProcessingStep.get(nextStepCode)) {

            case FLOW_COMPLETE:
              // If   : Next step = Hoàn thành phê duyệt
              // Then : Bật popup nhập OTP xác nhận phê duyệt hồ sơ
                // Check CIFI
              apParamRepository.findByCodeAndType(CBT_RUN_PROCESS_STATUS, CBT_RUN_PROCESS)
                      .ifPresent(conf -> verifyService.verifyCIFIStatus(bpmId));

              // check income credit ratings css
              verifyService.verifyIncomeCreditRatingsCss(applicationEntity);

              response.getPopupControl().setAcceptApproval(true);
              break;
            case APPROVE_PROPOSAL:
              // If   : Next step = Phê duyệt đề xuất
              // Then : Hiển thị cấp tiếp nhận & CB tiếp nhận
              // If   : Hồ sơ được BM trả về RM
              // Then : Cấp tiếp nhận = cấp tiếp nhận gần nhất, CB tiếp nhận = CB tiếp nhận gần nhất
              nextStepApprovalProposal(applicationEntity, response);
              break;
            case TL_COORDINATOR:
              // If   : Next step = Team lead
              // Then : Chỉ hiển thị cấp tiếp nhận = cấp phê duyệt khoản vay tại thời điểm hiện tại
              nextStepTeamLead(applicationEntity, response);
              break;
            case COORDINATOR:
            case APPROVE_PROFILE_1:
              // If   : Next step = CB điều phối/CB xử lý 1
              // Then : Hiển thị cấp tiếp nhận & CB tiếp nhận
              // If   : Hồ sơ được trả về RM
              // Then : Cấp tiếp nhận = cấp thẩm quyền phê duyệt khoản vay tại thời điểm hiện tại, CB tiếp nhận = CB tiếp nhận gần nhất
              String nextRole = ruleResponse.getRuleDataItem().getData().getNextRole();
              nextStepCoordinatorOrApprovalProfile(applicationEntity, nextRole, response);
              break;
            case APPROVE_PROFILE_2:
            case APPROVE_PROFILE_3:
            case COUNCIL_APPROVAL_1:
            case COUNCIL_APPROVAL_2:
            case COUNCIL_APPROVAL_3:
              // If   : Next step = CB xử lý 2 / CB xử lý 3 / HDTD NHBL/ HĐTD&ĐT / HĐQT
              // Then : Hiển thị cấp tiếp nhận & CB tiếp nhận
              response.getPopupControl().setShowAuthorityReception(true);
              response.getPopupControl().setShowUserReception(true);
              break;
            case MANAGE_PROFILES:
              // If   : Next step = KS HĐTD
              // Then : Chỉ hiển thị CB tiếp nhận
              response.getPopupControl().setShowUserReception(true);
              break;
            default:
              break;
          }

          log.info(
              "GetGenerateAndPreviewFormsServiceImpl.execute end with bpmId : {}, response : {}",
              bpmId, JsonUtil.convertObject2String(response, objectMapper));

          return response;
        })
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));
  }

  private void nextStepApprovalProposal(ApplicationEntity applicationEntity,
      PreviewFormDTO response) {
    response.getPopupControl().setShowAuthorityReception(true);
    response.getPopupControl().setShowUserReception(true);

    List<ApplicationHistoryApprovalEntity> approvalEntities = getPreviousHistoryApprovalByRole(
        new ArrayList<>(applicationEntity.getHistoryApprovals()), PD_RB_BM);

    if (CollectionUtils.isNotEmpty(approvalEntities)) {
      response.getPopupControl().setAuthorityReception(
          AuthorityDetailDTO.builder().code(approvalEntities.get(0).getProposalApprovalReception())
              .name(approvalEntities.get(0).getProposalApprovalReceptionTitle())
              .build());

      response.getPopupControl().setUserReception(
          UserReceptionDTO.builder().userName(approvalEntities.get(0).getProposalApprovalUser())
              .fullName(approvalEntities.get(0).getProposalApprovalFullName()).build());
    }
  }

  private void nextStepTeamLead(ApplicationEntity applicationEntity, PreviewFormDTO response) {
    response.getPopupControl().setAuthorityReception(
        AuthorityDetailDTO.builder().code(applicationEntity.getLoanApprovalPosition())
            .name(applicationEntity.getLoanApprovalPositionValue()).build());
  }

  private void nextStepCoordinatorOrApprovalProfile(ApplicationEntity applicationEntity,
      String nextRole, PreviewFormDTO response) {
    response.getPopupControl().setShowAuthorityReception(false);
    response.getPopupControl().setShowUserReception(false);

    if (applicationEntity.getGivebackRole() == null) {
      return;
    }

    response.getPopupControl().setAuthorityReception(
        AuthorityDetailDTO.builder().code(applicationEntity.getLoanApprovalPosition())
            .name(applicationEntity.getLoanApprovalPositionValue()).build());

    List<ApplicationHistoryApprovalEntity> approvalEntities = getPreviousHistoryApprovalByRole(
        new ArrayList<>(applicationEntity.getHistoryApprovals()), ProcessingRole.valueOf(nextRole));
    if (CollectionUtils.isNotEmpty(approvalEntities)) {
      response.getPopupControl().setUserReception(
          UserReceptionDTO.builder().userName(approvalEntities.get(0).getUsername())
              .fullName(approvalEntities.get(0).getFullName()).build());
    }
  }

}
