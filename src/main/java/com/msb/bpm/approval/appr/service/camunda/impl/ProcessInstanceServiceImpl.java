package com.msb.bpm.approval.appr.service.camunda.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.camunda.CamundaClient;
import com.msb.bpm.approval.appr.client.camunda.CamundaProperties;
import com.msb.bpm.approval.appr.enums.camunda.CamundaTopic;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.entity.ProcessInstanceEntity;
import com.msb.bpm.approval.appr.model.request.camunda.ProcessInstanceRequest;
import com.msb.bpm.approval.appr.repository.ProcessInstanceRepository;
import com.msb.bpm.approval.appr.service.camunda.ProcessInstanceService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.community.rest.client.dto.ProcessInstanceDto;
import org.camunda.community.rest.client.dto.TaskDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 21/5/2023, Sunday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessInstanceServiceImpl implements ProcessInstanceService {

  private final ProcessInstanceRepository processInstanceRepository;
  private final CamundaClient camundaClient;
  private final ObjectMapper objectMapper;
  private final CamundaProperties properties;

  @Override
  @Async
  public void updateSubProcessInstance(String processBusinessKey, String subProcessInstanceId,
      String subProcessDefinitionId, String subProcessKey, Integer subWorkflowVersion) {
    ProcessInstanceEntity processInstanceEntity = processInstanceRepository.findByProcessBusinessKey(processBusinessKey)
        .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_APPLICATION));

    processInstanceEntity.setSubProcessInstanceId(subProcessInstanceId);
    processInstanceEntity.setSubProcessDefinitionId(subProcessDefinitionId);
    processInstanceEntity.setSubProcessKey(subProcessKey);
    processInstanceEntity.setSubWorkflowVersion(subWorkflowVersion);
    processInstanceEntity.setUpdatedAt(LocalDateTime.now());
    processInstanceRepository.saveAndFlush(processInstanceEntity);
  }

  @Override
  public void updateSubProcessInstance(ProcessInstanceRequest request) {
    log.info(">>>> Process instance : {}", JsonUtil.convertObject2String(request, objectMapper));

    updateSubProcessInstance(request.getProcessBusinessKey(),
        request.getProcessInstanceId(), null,
        request.getProcessDefinitionKey(),
        null);
  }

  @Override
  @Transactional
  public void updateTask(String processInstanceId, TaskDto taskDto) {
    try {
      ProcessInstanceEntity processInstanceEntity = processInstanceRepository.findByProcessInstanceId(
              processInstanceId)
          .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_APPLICATION));
      processInstanceEntity.setTaskId(taskDto.getId());
      processInstanceEntity.setTaskDefinitionKey(taskDto.getTaskDefinitionKey());
      processInstanceEntity.setUpdatedAt(LocalDateTime.now());
      processInstanceRepository.saveAndFlush(processInstanceEntity);
    } catch (Exception e) {
      log.error("Update task execute failure: ", e);
    }
  }

  @Override
  @Async
  public void updateProcessInstance(String bpmId) {
    log.info(
        "ProcessInstanceServiceImpl.updateProcessInstance start with bpmId {}", bpmId);
    try {
      ProcessInstanceEntity processInstanceEntity = processInstanceRepository.findByBpmId(bpmId)
          .orElse(null);
      if (processInstanceEntity == null || StringUtils.isBlank(
          processInstanceEntity.getProcessInstanceId())) {
        log.warn("Not found processInstanceEntity...");
        return;
      }

      String processKey = properties.getSubscriptions().get(
          CamundaTopic.INITIALIZE_VARIABLE.name()).getProcessDefinitionKey();
      ProcessInstanceDto process = camundaClient.getProcessInstanceByProcessKey(processInstanceEntity.getProcessBusinessKey(), processKey);

      TaskDto nextTask;
      List<String> processInstanceIds;
      if (process != null) {
        processInstanceIds = Collections.singletonList(process.getId());
      } else {
        processInstanceIds = Collections.singletonList(processInstanceEntity.getProcessInstanceId());
      }

      nextTask = camundaClient.getTask(processInstanceIds, processInstanceEntity.getProcessBusinessKey());

      processInstanceEntity.setSubProcessInstanceId(process != null ? process.getId() : null);
      processInstanceEntity.setSubProcessDefinitionId(process != null ? process.getDefinitionId() : null);
      processInstanceEntity.setSubProcessKey(processKey);
      processInstanceEntity.setTaskId(nextTask.getId());
      processInstanceEntity.setTaskDefinitionKey(nextTask.getTaskDefinitionKey());
      processInstanceEntity.setUpdatedAt(LocalDateTime.now());

      processInstanceRepository.save(processInstanceEntity);

      log.info(
          "ProcessInstanceServiceImpl.updateProcessInstance end with bpmId {} , processBusinessKey {} , process : {} , next task : {}",
          bpmId, processInstanceEntity.getProcessBusinessKey(), process, nextTask);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
