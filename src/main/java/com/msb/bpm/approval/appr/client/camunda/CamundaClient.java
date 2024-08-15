package com.msb.bpm.approval.appr.client.camunda;

import java.util.List;
import java.util.Map;
import org.camunda.community.rest.client.dto.ProcessDefinitionDto;
import org.camunda.community.rest.client.dto.ProcessInstanceDto;
import org.camunda.community.rest.client.dto.ProcessInstanceWithVariablesDto;
import org.camunda.community.rest.client.dto.TaskDto;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.camunda.community.rest.client.invoker.ApiException;

public interface CamundaClient {

  ProcessInstanceWithVariablesDto startProcessInstance(String processKey, Integer version,
      Map<String, VariableValueDto> variables, boolean returnVariable) throws ApiException;

  Map<String, VariableValueDto> submitForm(String id, Map<String, VariableValueDto> variables,
      boolean returnVariable) throws ApiException;

  TaskDto getTask(List<String> processInstanceIds, String processBusinessKey) throws ApiException;

  ProcessInstanceDto getProcessInstance(String processKey, String processBusinessKey)
      throws ApiException;

  ProcessInstanceDto getProcessInstance(List<String> processInstanceIds, String processBusinessKey)
      throws ApiException;

  ProcessDefinitionDto getProcessDefinitionByKeyAndVersion(String processKey,
      Integer version) throws ApiException;

  ProcessDefinitionDto getProcessDefinitionById(String processDefinitionId) throws ApiException;

  ProcessInstanceDto getProcessInstanceById(String processInstanceId) throws ApiException ;

  VariableValueDto getProcessVariables(String processInstanceId, String varName) throws ApiException;

  VariableValueDto getProcessHistoryVariables(String processInstanceId, String varName)
      throws ApiException;

  ProcessInstanceDto getProcessInstanceByProcessKey(String processBusinessKey, String processKey)
      throws ApiException;
}