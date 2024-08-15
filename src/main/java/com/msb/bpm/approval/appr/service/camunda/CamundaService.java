package com.msb.bpm.approval.appr.service.camunda;

import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ProcessInstanceEntity;
import java.util.Map;
import org.camunda.community.rest.client.dto.ProcessInstanceWithVariablesDto;
import org.camunda.community.rest.client.dto.VariableValueDto;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 9/7/2023, Sunday
 **/
public interface CamundaService {

  ProcessInstanceWithVariablesDto startProcessInstance(String requestCode, String processDefinitionKey, Integer version);

  VariableValueDto getCurrentState(String processingRole, ProcessInstanceEntity processInstanceEntity, String varName);

  Map<String, VariableValueDto> completeTaskWithReturnVariables(ApplicationEntity applicationEntity,
      String nextStepCode);
}
