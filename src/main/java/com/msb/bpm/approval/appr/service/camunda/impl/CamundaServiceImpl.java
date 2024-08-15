package com.msb.bpm.approval.appr.service.camunda.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.camunda.CamundaClient;
import com.msb.bpm.approval.appr.client.camunda.CamundaProperties;
import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.enums.camunda.CamundaTopic;
import com.msb.bpm.approval.appr.enums.camunda.CamundaVariable;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ProcessInstanceEntity;
import com.msb.bpm.approval.appr.service.camunda.ProcessInstanceService;
import com.msb.bpm.approval.appr.service.camunda.CamundaService;
import com.msb.bpm.approval.appr.util.CamundaUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.community.rest.client.dto.ProcessInstanceDto;
import org.camunda.community.rest.client.dto.ProcessInstanceWithVariablesDto;
import org.camunda.community.rest.client.dto.TaskDto;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 9/7/2023, Sunday
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class CamundaServiceImpl implements CamundaService {

  private final CamundaClient camundaClient;
  private final ProcessInstanceService processInstanceService;
  private final ObjectMapper objectMapper;
  private final CamundaProperties properties;

  @SneakyThrows
  @Override
  public ProcessInstanceWithVariablesDto startProcessInstance(String requestCode, String processDefinitionKey,
      Integer version) {

    Map<String, VariableValueDto> variables = new HashMap<>();
    variables.put(CamundaVariable.REQUEST_CODE.getValue(), new VariableValueDto().value(requestCode));

    ProcessInstanceWithVariablesDto processInstanceWithVariablesDto = camundaClient
        .startProcessInstance(processDefinitionKey, version, variables, true);

    log.info(">>>> Workflow process {} is started, process instance id {}", processDefinitionKey,
        processInstanceWithVariablesDto.getId());

    TaskDto nextTask = camundaClient.getTask(
        Collections.singletonList(processInstanceWithVariablesDto.getId()),
        processInstanceWithVariablesDto.getBusinessKey());

    log.info(">>>> Camunda workflow next task {}", nextTask);

    processInstanceWithVariablesDto.getVariables()
        .put("nextTask", new VariableValueDto().value(nextTask));

    return processInstanceWithVariablesDto;
  }

  @Override
  @SneakyThrows
  public VariableValueDto getCurrentState(String processingRole,
      ProcessInstanceEntity processInstanceEntity, String varName) {
    VariableValueDto variable;

    String processInstanceId;
    if (1 == processInstanceEntity.getWorkflowVersion()) {
      processInstanceId = PD_RB_RM.name().equalsIgnoreCase(processingRole)
          ? processInstanceEntity.getProcessInstanceId()
          : processInstanceEntity.getSubProcessInstanceId();
    } else {
      processInstanceId = StringUtils.isNotBlank(processInstanceEntity.getSubProcessInstanceId())
          ? processInstanceEntity.getSubProcessInstanceId()
          : processInstanceEntity.getProcessInstanceId();
    }

    try {
      variable = camundaClient.getProcessVariables(processInstanceId, varName);
    } catch (Exception e) {
      log.error(">>>> Can't get current state of process instance {} , {}", processInstanceId,
          e.getMessage());
      variable = camundaClient.getProcessHistoryVariables(processInstanceId, varName);
    }

    log.info(">>>> Current state of process instance {} , {}", processInstanceId, variable);

    return variable;
  }

  @SneakyThrows
  @Override
  public Map<String, VariableValueDto> completeTaskWithReturnVariables(
      ApplicationEntity applicationEntity, String nextStepCode) {

    log.info(">>>> Application : {} submit task with processInstanceId : {} , nextStepCode : {}",
        applicationEntity.getBpmId(), applicationEntity.getProcessInstance().getProcessInstanceId(),
        nextStepCode);

    log.info(">>>> Application : {} current state : {}", applicationEntity.getBpmId(),
        applicationEntity.getStatus());

    Map<String, VariableValueDto> response;

    ProcessInstanceEntity processInstanceEntity = applicationEntity.getProcessInstance();

    boolean isOldVersion = 1 == processInstanceEntity.getWorkflowVersion();

    log.info(">>>> Camunda workflow is old version : {}", isOldVersion);

    // Lấy lại trạng thái hiện tại của camunda workflow
    VariableValueDto varState = getCurrentState(applicationEntity.getProcessingRole(),
        processInstanceEntity,
        CamundaVariable.STATUS.getValue());

    log.info(">>>> Camunda workflow current state : {}", varState.getValue());

    if (isOldVersion) {
      if (Objects.equals(applicationEntity.getStatus(), varState.getValue())) {
        List<String> processInstanceIds = new ArrayList<>(Collections.singletonList(
            processInstanceEntity.getProcessInstanceId()));

        String processKey = properties.getSubscriptions().get(
            CamundaTopic.INITIALIZE_VARIABLE.name()).getProcessDefinitionKey();
        ProcessInstanceDto currentProcess = camundaClient.getProcessInstanceByProcessKey(
            applicationEntity.getProcessInstance().getProcessBusinessKey(), processKey);

        if (currentProcess != null) {
          processInstanceIds.add(currentProcess.getId());
        }

        response = completeTaskWithReturnVariables(processInstanceIds,
            processInstanceEntity.getProcessBusinessKey(),
            CamundaUtil.buildNextStepVariables(nextStepCode));
      } else {
        throw new ApprovalException(DomainCode.CAMUNDA_STATUS_ERROR);
      }
    } else {
      response = completeTaskWithReturnVariablesNewVersion(
          applicationEntity, varState, nextStepCode);
    }

    return response;
  }

  @SneakyThrows
  private Map<String, VariableValueDto> completeTaskWithReturnVariables(List<String> processInstanceIds,
      String processBusinessKey,
      Map<String, VariableValueDto> variables) {
    TaskDto currentTask = camundaClient.getTask(processInstanceIds, processBusinessKey);

    Map<String, VariableValueDto> variableMapResponse = camundaClient.submitForm(
        currentTask.getId(), variables, Boolean.TRUE);

    log.info(">>>> Camunda task {} is completed with variable [{}]", currentTask.getId(), variables);

    return variableMapResponse;
  }

  @SneakyThrows
  public Map<String, VariableValueDto> completeTaskWithReturnVariablesNewVersion(
      ApplicationEntity applicationEntity, VariableValueDto varState, String nextStepCode) {

    Map<String, VariableValueDto> response;

    if (Objects.equals(applicationEntity.getStatus(), varState.getValue())) {
      response = camundaClient.submitForm(applicationEntity.getProcessInstance().getTaskId(),
          CamundaUtil.buildNextStepVariables(nextStepCode), Boolean.TRUE);

      log.info(">>>> Camunda workflow next state then submit task : {}",
          CamundaUtil.getStatus(response));

      TaskDto taskDto;
      if (ApplicationStatus.isComplete(CamundaUtil.getStatus(response))) {
        taskDto = new TaskDto().id(CamundaVariable.END_TASK.getValue()).taskDefinitionKey(CamundaVariable.END_TASK.getValue());
      } else {

        String processKey = properties.getSubscriptions().get(
            CamundaTopic.INITIALIZE_VARIABLE.name()).getProcessDefinitionKey();
        ProcessInstanceDto currentProcess = camundaClient.getProcessInstanceByProcessKey(
            applicationEntity.getProcessInstance().getProcessBusinessKey(), processKey);

        log.info(">>>> Camunda workflow current processInstance : {}",
            JsonUtil.convertObject2String(currentProcess, objectMapper));

        List<String> processInstanceIds = new ArrayList<>();
        processInstanceIds.add(applicationEntity.getProcessInstance().getProcessInstanceId());
        if (currentProcess != null) {
          processInstanceIds.add(currentProcess.getId());
          applicationEntity.getProcessInstance().setSubProcessInstanceId(currentProcess.getId());
          applicationEntity.getProcessInstance().setSubProcessKey(processKey);
          applicationEntity.getProcessInstance()
              .setSubProcessDefinitionId(currentProcess.getDefinitionId());
        } else {
          applicationEntity.getProcessInstance().setSubProcessInstanceId(null);
          applicationEntity.getProcessInstance().setSubProcessDefinitionId(null);
        }

        taskDto = camundaClient.getTask(processInstanceIds,
            applicationEntity.getProcessInstance().getProcessBusinessKey());

        log.info(">>>> Camunda workflow next task : {}",
            JsonUtil.convertObject2String(taskDto, objectMapper));
      }

      applicationEntity.getProcessInstance().setTaskId(taskDto.getId());
      applicationEntity.getProcessInstance().setTaskDefinitionKey(taskDto.getTaskDefinitionKey());

      response.put("currentTask",
          new VariableValueDto().value(JsonUtil.convertObject2String(taskDto, objectMapper)));

    } else {
      throw new ApprovalException(DomainCode.CAMUNDA_STATUS_ERROR);
    }

    return response;
  }
}
