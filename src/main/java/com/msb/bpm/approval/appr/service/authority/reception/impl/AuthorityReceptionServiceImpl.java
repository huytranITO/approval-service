package com.msb.bpm.approval.appr.service.authority.reception.impl;

import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.APPROVE_PROPOSAL;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.COUNCIL_APPROVAL_1;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.COUNCIL_APPROVAL_2;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.COUNCIL_APPROVAL_3;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.isNextStepGetAuthoritiesGreaterCurrAuthority;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.isNextStepGetCurrAuthority;
import static com.msb.bpm.approval.appr.enums.authority.AuthorityCode.TQC10;
import static com.msb.bpm.approval.appr.enums.authority.AuthorityCode.TQC11;
import static com.msb.bpm.approval.appr.enums.authority.AuthorityCode.TQC12;
import static com.msb.bpm.approval.appr.enums.authority.AuthorityCode.TQC23;
import static com.msb.bpm.approval.appr.enums.authority.AuthorityCode.TQC24;
import static com.msb.bpm.approval.appr.enums.authority.AuthorityType.FACT;
import static com.msb.bpm.approval.appr.enums.authority.AuthorityType.SUGT;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;

import com.msb.bpm.approval.appr.enums.authority.AuthorityStatus;
import com.msb.bpm.approval.appr.enums.camunda.ButtonEventCode;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.mapper.DecisionRuleMapping;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDetailDTO;
import com.msb.bpm.approval.appr.model.dto.rule.TransitionConditionDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.authority.reception.AuthorityReceptionService;
import com.msb.bpm.approval.appr.service.intergated.AuthorityService;
import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 19/6/2023, Monday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorityReceptionServiceImpl extends AbstractBaseService implements AuthorityReceptionService {

  private final AuthorityService authorityService;
  private final DecisionRuleIntegrateService decisionRuleIntegrateService;
  private final ApplicationRepository applicationRepository;

  /**
   * Lấy dan sách cấp thẩm quyền tiếp nhận hồ sơ
   *
   * @param bpmId   String Mã hồ sơ
   * @return        List<AuthorityDetailDTO> Danh sách cấp thẩm quyền
   */
  @Override
  @Transactional
  public List<AuthorityDetailDTO> getAuthoritiesByApplication(String bpmId) {
    ApplicationEntity applicationEntity = applicationRepository.findByBpmId(bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));

    // Gọi rule workflow để xác định next step
    TransitionConditionDTO transitionCondition = DecisionRuleMapping.INSTANCE.toTransitionCondition(
            applicationEntity).withCredits(buildCredits(applicationEntity.getCredits()))
        .withEventCode(ButtonEventCode.SUCCESS.getCode());
    RuleResponse ruleResponse = (RuleResponse) decisionRuleIntegrateService.getDecisionRule(
        applicationEntity.getId(), RuleCode.R003, transitionCondition);

    String stepCode = ruleResponse.getRuleDataItem().getData().getStepCode();

    String customerType = applicationEntity.getCustomer().getCustomerType();

    // If   : Next step = Phê duyệt đề xuất
    // Then : Lấy danh sách cấp thẩm quyền là TQC23 & TQC24
    if (APPROVE_PROPOSAL.getCode().equalsIgnoreCase(stepCode)) {
      return authorityService.filterAuthority(SUGT.name(), null, customerType,
              AuthorityStatus.ACTIVE.name())
          .stream().filter(authority -> Arrays.asList(TQC23.name(), TQC24.name())
              .contains(authority.getCode()))
          .collect(Collectors.toList());
    }

    // If   : Next step = Team lead điều phối / điều phối viên / CB xử lý 1
    // Then : lấy danh sách cấp thẩm quyền là thẩm quyền phê duyệt khoản vay hiện tại của hồ sơ
    if (isNextStepGetCurrAuthority(stepCode)) {
      return authorityService.filterAuthority(FACT.name(), null, customerType,
              AuthorityStatus.ACTIVE.name()).stream().filter(
              authority -> authority.getCode()
                  .equalsIgnoreCase(applicationEntity.getLoanApprovalPosition()))
          .collect(Collectors.toList());
    }

    // If   : Next step = CB xử lý 2 / CB xử lý 3
    // Then : lấy danh sách cấp thẩm quyền có độ ưu tiên cao hơn cấp thẩm quyền phê duyệt khoản vay hiện tại của hồ sơ
    if (isNextStepGetAuthoritiesGreaterCurrAuthority(stepCode)) {
      return authorityService.filterAuthority(FACT.name(),
          applicationEntity.getLoanApprovalPosition(), customerType, AuthorityStatus.ACTIVE.name());
    }

    // If   : Next step = HDTD NHBL
    // Then : lấy danh sách cấp thẩm quyền là TQC10 - HDTD NHBL
    if (COUNCIL_APPROVAL_1.getCode().equalsIgnoreCase(stepCode)) {
      return authorityService.filterAuthority(FACT.name(), null, customerType,
              AuthorityStatus.ACTIVE.name())
          .stream()
          .filter(authority -> authority.getCode().equalsIgnoreCase(TQC10.name()))
          .collect(Collectors.toList());
    }

    // If   : Next step =  HĐTD&ĐT
    // Then : lấy danh sách cấp thẩm quyền là TQC11 - HĐTD & ĐT
    if (COUNCIL_APPROVAL_2.getCode().equalsIgnoreCase(stepCode)) {
      return authorityService.filterAuthority(FACT.name(), null, customerType,
              AuthorityStatus.ACTIVE.name()).stream().filter(
              authority -> authority.getCode()
                  .equalsIgnoreCase(TQC11.name()))
          .collect(Collectors.toList());
    }

    // If   : Next step =  HĐQT
    // Then : lấy danh sách cấp thẩm quyền là TQC12 - HĐQT
    if (COUNCIL_APPROVAL_3.getCode().equalsIgnoreCase(stepCode)) {
      return authorityService.filterAuthority(FACT.name(), null, customerType,
              AuthorityStatus.ACTIVE.name()).stream().filter(
              authority -> authority.getCode()
                  .equalsIgnoreCase(TQC12.name()))
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }
}
