package com.msb.bpm.approval.appr.repository;

import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_BM;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;

import com.msb.bpm.approval.appr.enums.application.GeneratorStatusEnums;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.application.ProcessingStep;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import com.msb.bpm.approval.appr.util.CamundaUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long>,
    JpaSpecificationExecutor<ApplicationEntity> {

  @EntityGraph(attributePaths = {"customer", "customer.customerIdentitys", "processInstance",
      "credits", "credits.creditCard", "credits.creditLoan", "credits.creditOverdraft"})
  Optional<ApplicationEntity> findByBpmId(String bpmId);

  @Query(value = "SELECT max(ae.id) FROM ApplicationEntity ae")
  Long getMaxId();
  Optional<List<ApplicationEntity>> findByCustomerRefCustomerIdIn(List<Long> refCustomerId);
  Optional<List<ApplicationEntity>> findByCustomerIdIn(List<Long> customerIds);

  @Query("SELECT a FROM ApplicationEntity a WHERE a.bpmId IN :bpmIds")
  Optional<List<ApplicationEntity>> findByBpmIdIn(List<String> bpmIds);

  @Query("SELECT a FROM ApplicationEntity a WHERE a.processInstance.processInstanceId = :id")
  Optional<ApplicationEntity> findByProcessInstanceId(String id);

  @Transactional
  default void saveData(ApplicationEntity applicationEntity, RuleResponse ruleResponse,
      String receptionUser) {

    // Set giveback role is processing role when return application to RM
    if (PD_RB_RM.name().equalsIgnoreCase(ruleResponse.getRuleDataItem().getData().getNextRole())) {
      applicationEntity.setGivebackRole(
          ProcessingRole.valueOf(applicationEntity.getProcessingRole()));
    } else {
      applicationEntity.setGivebackRole(null);
    }

    // Set next status
    applicationEntity.setStatus(ruleResponse.getRuleDataItem().getData().getNextTask());

    // Set next step
    applicationEntity.setProcessingStep(ruleResponse.getRuleDataItem().getData().getNextStep());

    // Set previous role
    applicationEntity.setPreviousRole(applicationEntity.getProcessingRole());

    // Set next role
    applicationEntity.setProcessingRole(ruleResponse.getRuleDataItem().getData().getNextRole());

    // Assign next user
    applicationEntity.setAssignee(receptionUser);

    save(applicationEntity);
  }

  @Transactional
  default void saveData(ApplicationEntity applicationEntity, RuleResponse ruleResponse,
      Map<String, VariableValueDto> variables, String receptionUser,
      List<CategoryDataResponse> dataResponses) {

    // Nếu  : role hiện tại không phải là PD_RB_BM & nextRole là PD_RB_RM
    // Thì  : Set giveBackRole = role hiện tại
    String nextRole = ruleResponse.getRuleDataItem().getData().getNextRole();
    if (StringUtils.isNotBlank(nextRole)
        && !PD_RB_BM.name().equalsIgnoreCase(applicationEntity.getProcessingRole())
        && PD_RB_RM.name().equalsIgnoreCase(nextRole)) {
      applicationEntity.setGivebackRole(
          ProcessingRole.valueOf(applicationEntity.getProcessingRole()));
    }

    // Set next status
    applicationEntity.setStatus(CamundaUtil.getStatus(variables));

    String stepCode = ruleResponse.getRuleDataItem().getData().getStepCode();

    // Set next step code
    applicationEntity.setProcessingStepCode(stepCode);

    // Set next step
    applicationEntity.setProcessingStep(Util.getProcessingStep(stepCode, dataResponses));

    // Set receptionAt
    if (!ProcessingStep.FLOW_COMPLETE.getCode().equalsIgnoreCase(stepCode)) {
      applicationEntity.setReceptionAt(LocalDateTime.now());
    }

    // Set previous role
    applicationEntity.setPreviousRole(applicationEntity.getProcessingRole());

    // Set next role
    if (StringUtils.isNotBlank(nextRole)) {
      applicationEntity.setProcessingRole(nextRole);
    }

    // Assign next user
    applicationEntity.setAssignee(receptionUser);

    // Set trạng thái generate biên bản phê duyệt/tờ trình về mặc định = 00
    applicationEntity.setGeneratorStatus(GeneratorStatusEnums.DEFAULT.getValue());

    saveAndFlush(applicationEntity);
  }

  Optional<List<ApplicationEntity>> findByCustomerBpmCifIn(List<String> bpmCifs);


  @Modifying
  @Query(value = "UPDATE application a SET a.created_by = :createBy"
      + ", a.assignee = :createBy WHERE a.id = :id", nativeQuery = true)
  void updateCreateBy(String createBy, Long id);

  @Transactional(readOnly = true)
  Optional<List<ApplicationEntity>> findAppByRefIdAndSource(String refId, String source);

  @Transactional(readOnly = true)
  Optional<List<ApplicationEntity>> findAppByRefId(String refId);

  @Query("SELECT a FROM ApplicationEntity a WHERE a.bpmId = :bpmId")
  Optional<ApplicationEntity> findByBpmIdCustomQuery(String bpmId);

  @Transactional
  @Modifying
  @Query(value = "UPDATE ApplicationEntity SET ldpStatus = :ldpStatus WHERE bpmId = :bpmId")
  void updateLdpStatus(@Param("ldpStatus") String ldpStatus, @Param("bpmId") String bpmId);

  Optional<List<ApplicationEntity>> findByIdInAndCustomerBpmCifIn(List<Long> applicationIdList, List<String> bpmCifs);

  Optional<List<ApplicationEntity>> findByIdInAndCustomerRefCustomerIdIn(List<Long> applicationIdList, List<Long> refCustomerIdList);
}
