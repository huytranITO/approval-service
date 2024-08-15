package com.msb.bpm.approval.appr.handler.camunda.sbo;

import com.msb.bpm.approval.appr.service.camunda.ProcessInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 21/5/2023, Sunday
 **/
@Component
@ExternalTaskSubscription("INITIALIZE_VARIABLE")
@RequiredArgsConstructor
@Slf4j
public class SBOInitializeVariableHandler implements ExternalTaskHandler {

  private final ProcessInstanceService processInstanceService;

  @Override
  public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
    try {
      log.info(">>>> Initialize variables sub-workflow {} with business key {}",
          externalTask.getProcessDefinitionKey(), externalTask.getBusinessKey());

      updateSubProcessInstance(externalTask);

      externalTaskService.complete(externalTask);

      log.info(">>>> Initialize variables is completed");
    } catch (Throwable e) {
      log.error(
          "Initialize sub workflow, with process instance id {} and business key {} failure, ",
          externalTask.getProcessInstanceId(), externalTask.getBusinessKey(), e);
      externalTaskService.handleFailure(externalTask, "Initialize sub workflow failure",
          e.getMessage(), 3, 60000);
    }
  }

  public void updateSubProcessInstance(ExternalTask externalTask) {
    processInstanceService.updateSubProcessInstance(externalTask.getBusinessKey(),
        externalTask.getProcessInstanceId(), externalTask.getProcessDefinitionId(), externalTask.getProcessDefinitionKey(),
        null);
  }
}
