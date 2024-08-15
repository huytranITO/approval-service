package com.msb.bpm.approval.appr.service.camunda;

import com.msb.bpm.approval.appr.model.request.camunda.ProcessInstanceRequest;
import org.camunda.community.rest.client.dto.TaskDto;
import org.camunda.community.rest.client.invoker.ApiException;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 21/5/2023, Sunday
 **/
public interface ProcessInstanceService {
  void updateSubProcessInstance(String processBusinessKey, String subProcessInstanceId,
      String subProcessDefinitionId, String subProcessKey, Integer subWorkflowVersion);

  void updateSubProcessInstance(ProcessInstanceRequest request) throws ApiException;

  void updateTask(String processInstanceId, TaskDto taskDto);

  void updateProcessInstance(String bpmId);
}
