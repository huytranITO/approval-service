package com.msb.bpm.approval.appr.service.user.reception.impl;

import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_CA1;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_CONTACT;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.isNextStepGetUserByAuthority;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.isNextStepGetUserByRoles;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.util.Util.distinctByKey;
import static java.util.Comparator.comparing;

import com.msb.bpm.approval.appr.client.authority.AuthorityClient;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.enums.application.AssignType;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.camunda.ButtonEventCode;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.mapper.DecisionRuleMapping;
import com.msb.bpm.approval.appr.model.dto.authority.UserReceptionDTO;
import com.msb.bpm.approval.appr.model.dto.rule.TransitionConditionDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetRoleUserResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
import com.msb.bpm.approval.appr.service.user.reception.UserReceptionService;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 12/6/2023, Monday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class UserReceptionServiceImpl extends AbstractBaseService implements UserReceptionService {

  private final AuthorityClient client;
  private final ApplicationRepository applicationRepository;
  private final DecisionRuleIntegrateService decisionRuleIntegrateService;
  private final UserManagerClient userManagerClient;

  /**
   * Lấy danh sách user theo mã thẩm quyềm
   *
   * @param bpmId String  Mã hồ sơ
   * @param code  String  Mã thẩm quyền
   * @return List<UserReceptionDTO>
   */
  @Override
  @Transactional
  public List<UserReceptionDTO> getUserByAuthority(String bpmId, String code) {
    ApplicationEntity applicationEntity = applicationRepository.findByBpmId(bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));

    // Gọi rule workflow để xác định next step
    TransitionConditionDTO transitionCondition = DecisionRuleMapping.INSTANCE.toTransitionCondition(
            applicationEntity).withCredits(buildCredits(applicationEntity.getCredits()))
        .withEventCode(ButtonEventCode.SUCCESS.getCode());
    RuleResponse ruleResponse = (RuleResponse) decisionRuleIntegrateService.getDecisionRule(
        applicationEntity.getId(), RuleCode.R003, transitionCondition);

    String stepCode = ruleResponse.getRuleDataItem().getData().getStepCode();

    List<UserReceptionDTO> responses = Collections.emptyList();

    // If   : Next step = CB xử lý 1 / CB xử lý 2 / CB xử lý 3 / HĐTD&ĐT / HĐQT / HDTD NHBL
    // Then : Lấy danh sách user mã thẩm quyền
    if (isNextStepGetUserByAuthority(stepCode)) {
      responses = client.getUserByAuthority(code, HeaderUtil.getToken()).getUserReceptions();
    }

    // If   : Next step = Phê duyệt đề xuất / KS HĐTD
    // Then : Lấy danh sách user theo roles
    // Riêng trường hợp next step = Phê duyệt đề xuất thì sẽ lấy theo cả mã ĐVKD của RM
    if (isNextStepGetUserByRoles(stepCode)) {

      boolean isRoleRM = PD_RB_RM.name().equalsIgnoreCase(applicationEntity.getProcessingRole());

      String nextRole = ruleResponse.getRuleDataItem().getData().getNextRole();
      String organizationCode = isRoleRM ? applicationEntity.getBusinessCode() : null;

      GetRoleUserResponse response = userManagerClient.getUserByRoles(
          Collections.singleton(nextRole), organizationCode);

      if (isRoleRM) {
        responses = response.getValue()
            .stream()
            .filter(filterUser -> !SecurityContextUtil.getCurrentUser()
                .equalsIgnoreCase(filterUser.getUsername()))
            .map(user -> new UserReceptionDTO(user.getUsername(), user.getFullName(), user.getStatus()))
            .collect(Collectors.toList());
      } else {
        responses = response.getValue()
            .stream()
            .map(user -> new UserReceptionDTO(user.getUsername(), user.getFullName(), user.getStatus()))
            .collect(Collectors.toList());
      }
    }

    return responses.stream().sorted(comparing(UserReceptionDTO::getUserName))
        .collect(Collectors.toList());
  }

  /**
   * Lấy danh sách user tiếp nhận hồ sơ bị trả về
   *
   * @param bpmId String
   * @return List<UserReceptionDTO>
   */
  @Override
  @Transactional
  public List<UserReceptionDTO> getReworkUserByApplication(String bpmId) {
    return Optional.of(applicationRepository.findByBpmId(bpmId)).filter(Optional::isPresent)
        .map(Optional::get)
        .map(applicationEntity -> {

          RuleResponse workflowRuleResponse = (RuleResponse) decisionRuleIntegrateService.getDecisionRule(
              applicationEntity.getId(), RuleCode.R003,
              DecisionRuleMapping.INSTANCE.toTransitionCondition(applicationEntity)
                  .withCredits(buildCredits(applicationEntity.getCredits()))
                  .withEventCode(ButtonEventCode.REWORK.getCode()));

          return getReworkUsers(applicationEntity.getProcessingRole(), workflowRuleResponse,
              applicationEntity.getHistoryApprovals());

        })
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));
  }

  /**
   * Lấy danh sách CBĐP / CBXL để gán hồ sơ assignType = COORDINATOR (CB điều phối) assignType =
   * HANDLING_OFFICER  (CB xử lý)
   *
   * @param assignType AssignType
   * @return List<UserReceptionDTO>
   */
  @Override
  public List<UserReceptionDTO> getAssignUsers(AssignType assignType) {
    String role =
        AssignType.COORDINATOR.equals(assignType) ? PD_RB_CONTACT.name() : PD_RB_CA1.name();

    return getUsersByRole(role);
  }

  /**
   * Lấy danh sách user để gán hồ sơ theo role tùy chỉnh truyền lên từ FE
   *
   * @param processingRole ProcessingRole
   * @return List<UserReceptionDTO>
   */
  @Override
  public List<UserReceptionDTO> getAssignUsersByRole(ProcessingRole processingRole) {
    return getUsersByRole(processingRole.name());
  }

  private List<UserReceptionDTO> getReworkUsers(String processingRole,
      RuleResponse workflowRuleResponse,
      Set<ApplicationHistoryApprovalEntity> historyApprovalEntities) {

    if (Objects.isNull(workflowRuleResponse)
        || Objects.isNull(workflowRuleResponse.getRuleDataItem())
        || Objects.isNull(workflowRuleResponse.getRuleDataItem().getData())) {
      return Collections.emptyList();
    }

    if (CollectionUtils.isEmpty(historyApprovalEntities) || PD_RB_RM.name()
        .equalsIgnoreCase(processingRole)) {
      return Collections.emptyList();
    }

    String nextRole = workflowRuleResponse.getRuleDataItem().getData().getNextRole();

    return historyApprovalEntities.stream().filter(
            historyApprovalEntity -> nextRole.equalsIgnoreCase(
                historyApprovalEntity.getUserRole().name()))
        .sorted(comparing(ApplicationHistoryApprovalEntity::getExecutedAt).reversed()
            .thenComparing(ApplicationHistoryApprovalEntity::getUsername))
        .filter(distinctByKey(ApplicationHistoryApprovalEntity::getUsername)).map(
            historyApprovalEntity -> new UserReceptionDTO(historyApprovalEntity.getUsername(),
                historyApprovalEntity.getFullName(), historyApprovalEntity.getStatus())).collect(Collectors.toList());
  }

  private List<UserReceptionDTO> getUsersByRole(String role) {
    GetRoleUserResponse response = userManagerClient.getUserByRoles(Collections.singleton(role),
        null);

    return response.getValue().stream()
        .map(user -> new UserReceptionDTO(user.getUsername(), user.getFullName(), user.getStatus()))
        .sorted(comparing(UserReceptionDTO::getUserName))
        .collect(Collectors.toList());
  }
}
